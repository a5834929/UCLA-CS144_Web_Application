package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}
    
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try{
	        String itemID = request.getParameter("itemID");
	        AuctionSearch as = new AuctionSearch();
	        String result = as.getXMLDataForItemId(itemID);

	        // if item not found
	        if(result.length()==0){
	        	request.setAttribute("itemFound", "false");
	        	request.getRequestDispatcher("/notFound.jsp").forward(request,response);
	        	return;
	        }

	        // if item found
	        request.setAttribute("itemFound", "true");
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        StringReader reader = new StringReader(result);
	        Document doc = builder.parse(new InputSource(reader));
	        Element root = doc.getDocumentElement();
	        
	        // Create new Item, set ID, name, currently
	        Item item = new Item();
	        item.setItemID(Integer.parseInt(itemID));
	        item.setName(getElementTextByTagNameNR(root,"Name"));
	        item.setCurrently(getElementTextByTagNameNR(root,"Currently"));
	        
	        // Set item buy price
	        String buyPrice = getElementTextByTagNameNR(root,"Buy_Price");
	        if(buyPrice=="") item.setBuyPrice("None");
	        else item.setBuyPrice(buyPrice);
	        
	        // Set item first bid, number of bids, time, description
            item.setFirstBid(getElementTextByTagNameNR(root, "First_Bid"));
            item.setNumberOfBids(Integer.parseInt(getElementTextByTagNameNR(root, "Number_of_Bids")));
            item.setStarted(getElementTextByTagNameNR(root, "Started"));
            item.setEnds(getElementTextByTagNameNR(root, "Ends"));
            item.setDescription(getElementTextByTagNameNR(root, "Description"));
            
            // Set item location and country
            Element itemLocation = getElementByTagNameNR(root, "Location");
            item.setLocation(getElementText(itemLocation));
            item.setCountry(getElementTextByTagNameNR(root, "Country"));
            item.setLatitude(itemLocation.getAttribute("Latitude"));
            item.setLongitude(itemLocation.getAttribute("Longitude"));
            
            // Set item seller data
            Element itemSeller = getElementByTagNameNR(root, "Seller");
            User seller = new User();
            seller.setUserID(itemSeller.getAttribute("UserID"));
            seller.setRating(Integer.parseInt(itemSeller.getAttribute("Rating")));
            item.setSeller(seller);
            
            // Set item categories
            Element[] category = getElementsByTagNameNR(root, "Category");
            List<String> categoryList = new ArrayList<String>();
            for(int i=0;i<category.length;i++)
            	categoryList.add(getElementText(category[i]));
            item.setCategory(categoryList);
            
            // Set item bid data
            Element[] bids = getElementsByTagNameNR(getElementByTagNameNR(root, "Bids"), "Bid");
            List<Bid> bidList = new ArrayList<Bid>();
            for(int i=0;i<bids.length;i++){
            	Element itemBidder = getElementByTagNameNR(bids[i], "Bidder");
            	User bidder = new User();
            	bidder.setUserID(itemBidder.getAttribute("UserID"));
            	bidder.setRating(Integer.parseInt(itemBidder.getAttribute("Rating")));
            	
            	Element bidderLocation = getElementByTagNameNR(itemBidder, "Location");
            	bidder.setLocation(getElementText(bidderLocation));
            	Element bidderCountry = getElementByTagNameNR(itemBidder, "Country");
            	bidder.setCountry(getElementText(bidderCountry));
            	
            	Bid bid = new Bid();
            	bid.setBidder(bidder);
            	bid.setTime(getElementText(getElementByTagNameNR(bids[i], "Time")));
            	bid.setAmount(getElementText(getElementByTagNameNR(bids[i], "Amount")));
            	bidList.add(bid);
            }
            item.setBids(bidList);
            request.setAttribute("item", item);
            request.getRequestDispatcher("/showItem.jsp").forward(request,response);
            
    	}catch(IOException ex) {
    		System.out.println(ex);
    	}catch(ServletException ex) {
    		System.out.println(ex);
    	}catch(SAXException ex) {
    		System.out.println(ex);
    	}catch(ParserConfigurationException ex) {
    		System.out.println(ex);
    	}
    }
}
