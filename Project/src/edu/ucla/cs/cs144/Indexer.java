package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	/* structure for storing database data */
	public static class User{ 
        String name;
        String description;
        String category;
        User(String name, String description){ 
        	this.name = name; 
            this.description = description;
            this.category = new String();
        }

        public void addCategory(String category){
        	this.category = this.category.concat(" "+category);
        	return;
        }
    }

	/* */   
    static Map<Integer, User> userMap = new HashMap<Integer, User>();
    private IndexWriter indexWriter = null;
	
	/** Creates a new instance of Indexer */
	public Indexer() {}


	/* get or create the indexwriter */
	private void createIndexWriter() throws IOException{
		if (indexWriter == null) {
			Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index-basic"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);
		}
	}

	/* close the indexwriter */
	private void closeIndexWriter() throws IOException{
		if (indexWriter != null) {
		    indexWriter.close();
		}
	}
 
	public void rebuildIndexes() {

		Connection conn = null;

		// create a connection to the database to retrieve Items from MySQL
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}


		/*
		 * Add your code here to retrieve Items using the connection
		 * and add corresponding entries to your Lucene inverted indexes.
			 *
			 * You will have to use JDBC API to retrieve MySQL data from Java.
			 * Read our tutorial on JDBC if you do not know how to use JDBC.
			 *
			 * You will also have to use Lucene IndexWriter and Document
			 * classes to create an index and populate it with Items data.
			 * Read our tutorial on Lucene as well if you don't know how.
			 *
			 * As part of this development, you may want to add 
			 * new methods and create additional Java classes. 
			 * If you create new classes, make sure that
			 * the classes become part of "edu.ucla.cs.cs144" package
			 * and place your class source files at src/edu/ucla/cs/cs144/.
		 * 
		 */


		// use JDBC API to retrieve MySQL data from Java
		try{
			Statement stmt = conn.createStatement();
			ResultSet rsItem = stmt.executeQuery("SELECT * FROM Item");
			Integer itemID;
			String name, description, cateName;
			while (rsItem.next()) {
				itemID = rsItem.getInt("ItemID");
				name = rsItem.getString("Name");
				description = rsItem.getString("Description");
				User user = new User(name, description);
				userMap.put(itemID, user);
			}
			ResultSet rsCate = stmt.executeQuery("SELECT * FROM Category");
			while (rsCate.next()) {
				itemID = rsCate.getInt("ItemID");
				cateName = rsCate.getString("CateName");
				User user = userMap.get(itemID);
				user.addCategory(cateName);
			}
		} catch(SQLException e){
			System.out.println(e);
		}



		// Lucene IndexWriter create an index
		try{
			createIndexWriter();
			Iterator it = userMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, User> user = (Map.Entry<Integer, User>)it.next();
				String key = user.getKey().toString();
				User value = user.getValue();

				Document doc = new Document();
				String fullSearchableText = value.name + " " + value.category + " " + value.description;
				doc.add(new StringField("ItemID", key, Field.Store.YES));
				doc.add(new TextField("Name", value.name, Field.Store.YES));
				doc.add(new TextField("Category", value.category, Field.Store.YES));
				doc.add(new TextField("Description", value.description, Field.Store.YES));
				doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
				indexWriter.addDocument(doc);
			}
			closeIndexWriter();
		} catch(IOException e){
			System.out.println(e);
		}



		// close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}    

	public static void main(String args[]) {
		Indexer idx = new Indexer();
		idx.rebuildIndexes();
	}
}
