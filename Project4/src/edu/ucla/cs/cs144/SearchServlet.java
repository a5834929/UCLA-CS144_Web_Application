package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try{
	        String query = request.getParameter("query");
	        request.setAttribute("query", query);
	        String numToSkipStr = request.getParameter("numToSkip");
	        String numToReturnStr = request.getParameter("numToReturn");
	        int numResultsToSkip = Integer.parseInt(numToSkipStr);
	        int numResultsToReturn = Integer.parseInt(numToReturnStr);
	        
	        AuctionSearch as = new AuctionSearch();
	        SearchResult[] result = as.basicSearch(query, numResultsToSkip, numResultsToReturn);
			if(result.length!=0){
				request.setAttribute("itemFound", true);
				request.setAttribute("searchResult", result);
			        request.getRequestDispatcher("/showSearchResult.jsp").forward(request,response);
			}else{
				request.setAttribute("itemFound", false);
				request.getRequestDispatcher("/notFound.jsp").forward(request, response);
			}
    	}
    	catch(IOException ex) {
    		System.out.println(ex);
    	}
    	catch(ServletException ex) {
    		System.out.println(ex);
    	}
    }
}
