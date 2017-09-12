/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrt.master;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Marco Micera, Leonardo Bernardi
 */
public class JRTMasterDispatcher extends HttpServlet  {
    private HashMap<String, JRTMaster> masters;
    
    @Override
    public void init() {
        masters = new HashMap<String, JRTMaster>();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        String id = request.getParameter("id");
        
        if(!masters.containsKey(id))
            masters.put(id, new JRTMaster());
        
        masters.get(id).doGet(request, response);
        
        if(request.getParameter("command").split("\\s+")[0].compareTo("quit") == 0)
            masters.remove(id);
    }
    
    @Override
    public void destroy() {
        masters = null;
    }
}
