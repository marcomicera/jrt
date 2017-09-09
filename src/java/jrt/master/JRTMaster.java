/*
 * Copyright (C) 2017 Marco Micera, Leonardo Bernardi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jrt.master;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jrt.slave.JRTSlave;

/**
 *
 * @author Marco Micera, Leonardo Bernardi
 */

public class JRTMaster extends HttpServlet {
    private JRTSlave slave;
    private boolean connected;
    private String cmd, path;
    
    @Override
    public void init() {
        slave = null;
        connected = false;
        path = null;
    }
    
    @Override
    public void destroy() {
    }
    
    /**
     * It creates a connection between this master and a specified slave
     * @param ipAddr slave's IP address
     * @param port slave's port
     * @return a slave object or null if there was an error during connection
     */

    private JRTSlave connect(String ipAddr, int port) {
        try {
            return (JRTSlave)Naming.lookup("//" + ipAddr + ":" + port + "/RemoteTerminal");
        } catch(NotBoundException | MalformedURLException | RemoteException ex) {
            return null;
        }
    }
    
    private String[] executeCommand(JRTSlave slave, String cmd, String path) {
        try {
            if(path == null || path.equals("") == true)
                return slave.executeCommand("cmd /c " + cmd);
            else
                return slave.executeCommand("cmd /c " + cmd, path); 
            
        } catch (RemoteException ex) {
            Logger.getLogger(JRTMaster.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private String pwd(JRTSlave slave, String path) {
        String pwd = executeCommand(slave, "echo %cd%", path)[0];
        return pwd.trim();
    }
    
    private void print(PrintWriter writer, String text) {
        // String processing
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        text = text.replaceAll("\n", "<br />");
        text = text.replaceAll("\t", "&emsp;");
        text = "<pre class=\"response\">" + text + "</pre>";
        
        // Actual printing
        writer.println(text + ";;;" + path);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        
        //print(writer, "> ");
        cmd = request.getParameter("command");

        String[] splitted = cmd.split("\\s+");

        switch(splitted[0]) {
            case "connect":
                if(connected == true) {
                    print(writer, 
                        "You already connected to a slave.\n" +
                        "You can create a new tab above to connect to multiple slaves"
                    );
                    break;
                }
                if(splitted.length != 3) {
                    print(writer, 
                        "Usage: connect <ip_address> <port>"
                    );
                    break;
                }
                if(Long.parseLong(splitted[2]) > 65535 || Long.parseLong(splitted[2]) < 0) {
                    print(writer, "Invalid port number");
                    break;
                }
                slave = connect(splitted[1], Integer.parseInt(splitted[2]));
                if(slave == null) {
                    print(writer, "Something was wrong with the parameters: please, try again");
                    break;
                }
                path = pwd(slave, "./");
                
                print(writer, "Connection established successfully!");
                connected = true;
                break;

            case "help":
                print(writer, 
                    "Commands list:\n" + 
                    "connect &lt;ip_address&gt; &lt;port&gt;   Connects to the slave\n" +
                    "quit                          Closes a connection\n" +
                    "<command>                     If connected, sends a command to the slave\n" +
                    "help                          Prints this help message\n" +
                    "exit                          Terminates this program"
                );
                break;

            case "quit":
                if(connected == false) {
                    print(writer, "You can only quit a connection after you have established one first");
                    break;
                }
                connected = false;
                slave = null;

                break;

            case "exit":
                print(writer, "Terminating master program...");
                return;

            // Commands
            default:
                if(connected == false) {
                    print(writer, 
                        "Before typing a command, you must connect to a slave first:" +
                        "use the connect <ip_address> <port> command"
                    );
                    break;
                }

                String[] result = executeCommand(slave, cmd, path);
                path = result[1];
                print(writer, (result == null) ? "Command unsupported" : result[0]);
                break;
        }
    }
}
