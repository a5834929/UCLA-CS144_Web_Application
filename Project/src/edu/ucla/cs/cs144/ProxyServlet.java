package edu.ucla.cs.cs144;

import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
        try{
            String query = request.getParameter("query");

            URL url = new URL("http://google.com/complete/search?output=toolbar&q="+ URLEncoder.encode(query, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuffer buf = new StringBuffer();
            BufferedReader resContent = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = resContent.readLine()) != null) {
                buf.append(line + "\n");
            }
            resContent.close();
            conn.disconnect();

            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            out.write(buf.toString());
            out.close();
        } 
        catch (Exception e) {
        	// redirect to some page
        }
    }
}
