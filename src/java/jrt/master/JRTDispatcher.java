/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrt.master;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jrt.master.JRTMaster;

/**
 *
 * @author Marco Micera, Leonardo Bernardi
 */
public class JRTDispatcher extends HttpServlet {
    private ArrayList<JRTMaster> terminals;
    
    @Override
    public void init() {
        terminals = new ArrayList<JRTMaster>();
        terminals.add(0, new JRTMaster());
    }
    
    @Override
    public void destroy() {
        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int terminal = Integer.parseInt(request.getParameter("terminal"));
        
        // Checking for new terminal creation
        String cmd = request.getParameter("command");
        String[] splitted = cmd.split("\\s+");
        if(splitted[0].compareTo("new") == 0)
            terminals.add(terminal, new JRTMaster());
        else
            terminals.get(terminal - 1).doGet(request, response);
    }
}
