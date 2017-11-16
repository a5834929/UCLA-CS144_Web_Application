package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.Collector;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
		 * You may create helper functions or classes to simplify writing these
		 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
     */
	
	public String escapeXml(String str){
		String replacedStr = str.replaceAll("&", "&amp;");
		replacedStr = replacedStr.replaceAll("<", "&lt;");
		replacedStr = replacedStr.replaceAll(">", "&gt;");
		return replacedStr;
	}


	/* Part A 
		* Basic keyword search: 
		* return the itemId and name of all items that contain a set of keywords. 
		* The search should be performed over 
		* the union of the item name, category, and description attributes.
	*/
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {

		SearchResult[] basicResults = null;

		try{
			// get Lucene searcher
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open
				(FSDirectory.open(new File("/var/lib/lucene/index-basic"))));
			QueryParser parser = new QueryParser("content", new StandardAnalyzer());

			// search
			Query querys = parser.parse(query);
			TopDocs topDocs = searcher.search(querys, numResultsToSkip+numResultsToReturn);
			ScoreDoc[] hits = topDocs.scoreDocs;
			int len = 0;
			if(hits.length>=numResultsToSkip)	len = hits.length-numResultsToSkip;  
			basicResults = new SearchResult[len];

			// use SearchResult
			for (int i = 0; i < basicResults.length; i++) {
				Document doc = searcher.doc(hits[i+numResultsToSkip].doc);
				String itemID = doc.get("ItemID");
				String itemName = doc.get("Name");
				basicResults[i] = new SearchResult(itemID, itemName);
			}
		} catch(Exception e){
			System.out.println(e);
		}

		return basicResults;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		SearchResult[] basicResults = basicSearch(query, numResultsToSkip, numResultsToReturn); 
		SearchResult[] tmpResults = new SearchResult[numResultsToReturn];
		SearchResult[] spatialResults = null;
		
		Connection conn = null;
		// create a connection to the database to retrieve Items from MySQL
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		
		try{
			String polygon = "GeomFromText('Polygon(("+region.getLx()+" "+region.getLy()+", "
													  +region.getLx()+" "+region.getRy()+", "
													  +region.getRx()+" "+region.getRy()+", "
													  +region.getRx()+" "+region.getLy()+", "
													  +region.getLx()+" "+region.getLy()+"))')";
			
			String pstmtQuery = "SELECT ItemID FROM ItemSpatial WHERE MBRContains("+polygon+", Location) AND ItemID = ?";
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement(pstmtQuery);
			int count = 0;
			int start = numResultsToSkip;
			
			while(count<numResultsToReturn && basicResults.length>0){
				for(int i=0;i<basicResults.length;i++){
					pstmt.setString(1, basicResults[i].getItemId());
					ResultSet rs = pstmt.executeQuery();
					
					while(rs.next()){
						if(count>numResultsToReturn-1){
							break;
						}else{
							tmpResults[count] = basicResults[i];
							count++;
						}
					}
					rs.close();
				}
				start += numResultsToReturn;
				basicResults = basicSearch(query, start, numResultsToReturn);
			}
			
			int finalSize = Math.min(count, numResultsToReturn);
			spatialResults = new SearchResult[finalSize];
			for(int i=0;i<finalSize;i++)
				spatialResults[i] = tmpResults[i];

		}catch (SQLException e) {
	   		System.out.println(e);
	   	}
		
		// close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		
		return spatialResults;
	}

	/* Part B.4
	* return the XML-formatted item data as string given its itemID.
	* conforms to the DTD of the original eBay XML data.
	* if there is no matching Item for a given itemId, return an empty string.
	*/
	public String getXMLDataForItemId(String itemId) {
		
		// create a connection to the database to retrieve Items from MySQL
		Connection conn = null;
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	
	
		// use JDBC API to retrieve MySQL data from Java
		String xmlBuf = "";
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Item WHERE ItemID="+itemId);
			
			if(rs.next()){ // non-empty
	
				String itemBuf1 = ""; // itemID, name
				String itemBuf2 = ""; // currently, buy_Price?, first_Bid, number_of_Bids
				String itemBuf3 = ""; // Location, Country, Started, Ends
				String itemBuf4 = ""; // Description
				String name = escapeXml(rs.getString("Name"));
				String currently = String.format("%.2f", rs.getFloat("Currently"));
				String buy_Price = rs.getString("Buy_Price"); // change to varchar type !!
				String first_Bid = String.format("%.2f", rs.getFloat("First_Bid"));
				String number_of_Bids = String.valueOf(rs.getInt("Number_of_Bids"));
				String location = rs.getString("Location");
				String country = rs.getString("Country");
				String latitude = rs.getString("Latitude");
				String longitude = rs.getString("Longitude");
				String started = rs.getString("Started");
				String ends = rs.getString("Ends");
				String sellerID = rs.getString("SellerID");
				String description = escapeXml(rs.getString("Description"));
				itemBuf1 = itemBuf1.concat("<Item ItemID=\""+ itemId + "\">\n");
				itemBuf1 = itemBuf1.concat("<Name>"+ name + "</Name>\n");
				itemBuf2 = itemBuf2.concat("<Currently>$"+ currently + "</Currently>\n");
				if(!buy_Price.equals("")) itemBuf2 = itemBuf2.concat("<Buy_Price>$"+ buy_Price + "</Buy_Price>\n");
				itemBuf2 = itemBuf2.concat("<First_Bid>$"+ first_Bid + "</First_Bid>\n");
				itemBuf2 = itemBuf2.concat("<Number_of_Bids>"+ number_of_Bids + "</Number_of_Bids>\n");
				itemBuf3 = itemBuf3.concat("<Location");
				if(!latitude.equals("")) itemBuf3 = itemBuf3.concat(" Latitude=\""+latitude+"\"");
				if(!longitude.equals("")) itemBuf3 = itemBuf3.concat(" Longitude=\""+longitude+"\"");
				itemBuf3 = itemBuf3.concat(">" + location + "</Location>\n");
				itemBuf3 = itemBuf3.concat("<Country>" + country + "</Country>\n");
				itemBuf3 = itemBuf3.concat("<Started>" + started + "</Started>\n");
				itemBuf3 = itemBuf3.concat("<Ends>" + ends + "</Ends>\n");
				itemBuf4 = itemBuf4.concat("<Description>" + description + "</Description>\n");
	
				// Category
				String cateBuf = "";
				rs = stmt.executeQuery("SELECT * FROM Category WHERE ItemID="+itemId);
				while(rs.next()){
					String cateName = escapeXml(rs.getString("CateName"));
					cateBuf = cateBuf.concat("<Category>" + cateName + "</Category>\n");
				}
	
				// Bidder
				String bidderBuf = "";
				String bidsBuf = "";
				Statement stmtBid = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM Bidder WHERE ItemID="+itemId);
				while(rs.next()){
					bidsBuf = bidsBuf.concat("  <Bid>\n");
					String bidderID = rs.getString("BidderID");
					String bid_Time = rs.getString("Bid_Time");
					String amount = String.format("%.2f", rs.getFloat("Amount"));
	
					String bidLocation=null, bidCountry=null, bid_rating=null;
					ResultSet rsBid = stmtBid.executeQuery("SELECT * FROM User WHERE UserID=\""+bidderID+"\"");
					if(rsBid.next()){
						bidLocation = rsBid.getString("Location");
						bidCountry = rsBid.getString("Country");
						bid_rating = rsBid.getString("Bid_rating");
					}
	
					bidsBuf = bidsBuf.concat("    <Bidder Rating=\""+bid_rating+"\" UserID=\""+bidderID+"\">\n");
					if(bidLocation!=null) bidsBuf = bidsBuf.concat("      <Location>" + bidLocation + "</Location>\n");
					if(bidCountry!=null) bidsBuf = bidsBuf.concat("      <Country>" + bidCountry + "</Country>\n");
					bidsBuf = bidsBuf.concat("    </Bidder>\n");
					bidsBuf = bidsBuf.concat("    <Time>"+bid_Time+"</Time>\n");
					bidsBuf = bidsBuf.concat("    <Amount>$"+amount+"</Amount>\n");
	
					bidsBuf = bidsBuf.concat("  </Bid>\n");
				}
				if(bidsBuf.equals("")) bidderBuf = bidderBuf.concat("<Bids />\n");
				else bidderBuf = bidderBuf.concat("<Bids>\n" + bidsBuf + "</Bids>\n");
				
				// Seller: <Seller Rating="221" UserID="dollface94" />
				String rating = null;
				rs = stmt.executeQuery("SELECT * FROM User WHERE UserID=\""+sellerID+"\"");
				if(rs.next()) rating = rs.getString("Sell_Rating");
				String sellerBuf = "<Seller Rating=\""+rating+"\" UserID=\""+sellerID+"\" />\n";
	
				// pack into xmlBuf
				xmlBuf = xmlBuf.concat(itemBuf1);
				xmlBuf = xmlBuf.concat(cateBuf);
				xmlBuf = xmlBuf.concat(itemBuf2);
				xmlBuf = xmlBuf.concat(bidderBuf);
				xmlBuf = xmlBuf.concat(itemBuf3);
				xmlBuf = xmlBuf.concat(sellerBuf);
				xmlBuf = xmlBuf.concat(itemBuf4);
				xmlBuf = xmlBuf.concat("</Item>");
			}
			
		} catch(SQLException e){
			System.out.println(e);
		}
	
		// close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	
		return xmlBuf;
	}
	
	public String echo(String message) {
		return message;
	}

}
