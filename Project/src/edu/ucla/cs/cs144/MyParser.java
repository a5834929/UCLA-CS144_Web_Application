/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
	
    public static class User{ 
        String u_id;
        String u_location;
        String u_country;
        String u_brating;
        String u_srating;
        User(String id, String location, String country, String brating, String srating){ 
        	u_id = id; 
            u_location = location;
            u_country = country;
            u_brating = brating;
            u_srating = srating;
        } 
        
        public void setBidderRating(String rating){
        	u_brating = rating;
        }
        
        public void setSellerRating(String rating){
        	u_srating = rating;
        }
    }
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    static FileOutputStream outItem = null;
    static FileOutputStream outCategory = null;
    static FileOutputStream outBidder = null;
    static FileOutputStream outUser = null;
	static File itemFile;
	static File categoryFile;
	static File bidderFile;
	static File userFile;
	static Map<String, User> userMap = new HashMap<String, User>();
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) throws IOException {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        StringBuffer str;
        Element root = doc.getDocumentElement();
        Element[] item = getElementsByTagNameNR(root, "Item");
        int itemCnt = item.length;
        for(int i=0;i<itemCnt;i++){
        	
        	// Get item information
        	String itemID = item[i].getAttribute("ItemID");
        	String name =  getElementTextByTagNameNR(item[i], "Name");

        	String currently = strip(getElementTextByTagNameNR(item[i], "Currently"));
            String buyPrice = strip(getElementTextByTagNameNR(item[i], "Buy_Price"));
            if (buyPrice.isEmpty())	buyPrice = "";
            String firstBid = strip(getElementTextByTagNameNR(item[i], "First_Bid"));
            String numberBid = getElementTextByTagNameNR(item[i], "Number_of_Bids");
            
            String started = getElementTextByTagNameNR(item[i], "Started");
            String ends = getElementTextByTagNameNR(item[i], "Ends");
            String description = getElementTextByTagNameNR(item[i], "Description");
            if(description.length()>4000)	description = description.substring(0, 4000);
            
            Element eLocation = getElementByTagNameNR(item[i], "Location");
            String location = getElementText(eLocation);
            String country = getElementTextByTagNameNR(item[i], "Country");
            String latitude = eLocation.getAttribute("Latitude");
            if(latitude.isEmpty())	latitude = "";
            String longitude = eLocation.getAttribute("Longitude");
            if(longitude.isEmpty())	longitude = "";
            
            Element eSeller = getElementByTagNameNR(item[i], "Seller");
            String sellerID = eSeller.getAttribute("UserID");
            String sellerRating = eSeller.getAttribute("Rating");
            
            str = new StringBuffer(itemID+columnSeparator+name+columnSeparator+currently+columnSeparator+
            		buyPrice+columnSeparator+firstBid+columnSeparator+numberBid+columnSeparator+location+
            		columnSeparator+country+columnSeparator+latitude+columnSeparator+longitude+
            		columnSeparator+started+columnSeparator+ends+columnSeparator+sellerID+columnSeparator+
            		description+"\n");
            outItem.write(str.toString().getBytes());
            str = new StringBuffer(sellerID+columnSeparator+location+columnSeparator+
					country+columnSeparator+sellerRating+"\n");
            
            // Search for userID==sellerID. If found, update seller rating; insert otherwise
            if(userMap.containsKey(sellerID)){
            	User user = userMap.get(sellerID);
            	if(user.u_srating.equals("")){
            		user.setSellerRating(sellerRating);
            		userMap.put(sellerID, user);
            	}
            }else{
            	User user = new User(sellerID, location, country, "", sellerRating);
            	userMap.put(sellerID, user);
            }
            
            // Get item-category pairs
            Element[] category = getElementsByTagNameNR(item[i], "Category");
            int categoryCnt = category.length;
            for(int j=0;j<categoryCnt;j++){
            	String categoryName = getElementText(category[j]);
            	str = new StringBuffer(itemID+columnSeparator+categoryName+"\n");
            	outCategory.write(str.toString().getBytes());
            }
            
            // Get Bidders
            Element[] bid = getElementsByTagNameNR(getElementByTagNameNR(item[i], "Bids"), "Bid");
            int bidCnt = bid.length;
            for(int j=0;j<bidCnt;j++){
            	Element bidder = getElementByTagNameNR(bid[j], "Bidder");
            	String bidderRating = bidder.getAttribute("Rating");
            	String bidderID = bidder.getAttribute("UserID");
            	Element bLocation = getElementByTagNameNR(bidder, "Location");
            	String bidderLocation;
            	if(bLocation!=null)
            		bidderLocation = getElementText(bLocation);
            	else	bidderLocation = "";
            		
            	Element bCountry = getElementByTagNameNR(bidder, "Country");
            	String bidderCountry;
            	if(bCountry!=null)
            		bidderCountry = getElementText(bCountry);
            	else	bidderCountry = "";
            	
            	String time = getElementText(getElementByTagNameNR(bid[j], "Time"));
            	String amount = strip(getElementText(getElementByTagNameNR(bid[j], "Amount")));
            	
            	str = new StringBuffer(itemID+columnSeparator+bidderID+columnSeparator+
            										time+columnSeparator+amount+"\n");
            	outBidder.write(str.toString().getBytes());
            	
                // Search for userID==bidderID. If found, update bidder rating; insert otherwise
                if(userMap.containsKey(bidderID)){
                	User user = userMap.get(bidderID);
                	if(user.u_brating.equals("")){
                		user.setBidderRating(bidderRating);
                		userMap.put(bidderID, user);
                	}
                }else{
                	User user = new User(bidderID, bidderLocation, bidderCountry, bidderRating, "");
                	userMap.put(bidderID, user);
                }
            }
        }
    }
    
    public static void main (String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
            
        	categoryFile = new File("Category.csv");
        	if (!categoryFile.exists()) 
        		categoryFile.createNewFile();
        	outCategory = new FileOutputStream(categoryFile);
        	
        	itemFile = new File("Item.csv");
        	if (!itemFile.exists()) 
        		itemFile.createNewFile();
        	outItem = new FileOutputStream(itemFile);
        	
        	bidderFile = new File("Bidder.csv");
        	if (!bidderFile.exists()) 
        		bidderFile.createNewFile();
        	outBidder = new FileOutputStream(bidderFile);
        	
        	userFile = new File("User.csv");
        	if (!userFile.exists()) 
        		userFile.createNewFile();
        	outUser = new FileOutputStream(userFile);
            
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
        
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
        	User userEntry = entry.getValue();
            StringBuffer str = new StringBuffer(userEntry.u_id+columnSeparator+userEntry.u_location+columnSeparator+
            		userEntry.u_country+columnSeparator+userEntry.u_brating+columnSeparator+
            		userEntry.u_srating+"\n");
            outUser.write(str.toString().getBytes());
        }
        
        outCategory.close();
        outBidder.close();
        outUser.close();
        outItem.close();
    }
}
