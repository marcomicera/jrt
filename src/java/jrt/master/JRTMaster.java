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
    /**
     * It creates a connection between this master and a specified slave
     * @param ipAddr slave's IP address
     * @param port slave's port
     * @return a slave object or null if there was an error during connection
     */

    private static JRTSlave connect(String ipAddr, int port) {
        try {
            return (JRTSlave)Naming.lookup("//" + ipAddr + ":" + port + "/RemoteTerminal");
        } catch(NotBoundException | MalformedURLException | RemoteException ex) {
            return null;
        }
    }
    
    private static String[] executeCommand(JRTSlave slave, String cmd, String path) {
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
    
    private static String pwd(JRTSlave slave, String path) {
        String pwd = executeCommand(slave, "echo %cd%", path)[0];
        return pwd.trim();
    }
    
    public static void main(String []args){
        // Checking command line input
        if(args.length != 0) {
            System.out.println("This program needs no command line arguments");
            return;
        }
        
        JRTSlave slave = null;
        boolean connected = false;
        Scanner scanner = new Scanner(System.in);
        String cmd, path = null;
        
        System.out.println("Master program started");
        
        while(true) {
            if(connected == true && slave != null)
                System.out.print(pwd(slave, path));
            System.out.print("> ");
            cmd = scanner.nextLine();
            
            String[] splitted = cmd.split("\\s+");
            
            switch(splitted[0]) {
                case "connect":
                    if(connected == true) {
                        System.out.println(
                            "You already connected to a slave.\n" +
                            "You can create a new tab above to connect to multiple slaves"
                        );
                        break;
                    }
                    if(splitted.length != 3) {
                        System.out.println(
                            "Usage: connect <ip_address> <port>"
                        );
                        break;
                    }
                    if(Long.parseLong(splitted[2]) > 65535 || Long.parseLong(splitted[2]) < 0) {
                        System.out.println("Invalid port number");
                        break;
                    }
                    slave = connect(splitted[1], Integer.parseInt(splitted[2]));
                    if(slave == null) {
                        System.out.println("Something was wrong with the parameters: please, try again");
                        break;
                    }
                    System.out.println("Connection established successfully!");
                    connected = true;
                    break;
                
                case "help":
                    System.out.println(
                        "Commands list:\n" +
                        "connect <ip_address> <port>\tConnects to the slave\n" +
                        "quit\t\t\t\tCloses a connection\n" +
                        "<command>\t\t\tIf connected, sends a command to the slave\n" +
                        "help\t\t\t\tPrints this help message\n" +
                        "exit\t\t\t\tTerminates this program"
                    );
                    break;
                    
                case "quit":
                    if(connected == false) {
                        System.out.println("You can only quit a connection after you have established one first");
                        break;
                    }
                    connected = false;
                    slave = null;
                    
                    break;
                    
                case "exit":
                    System.out.println("Terminating master program...");
                    return;
                
                // Commands
                default:
                    if(connected == false) {
                        System.out.println(
                            "Before typing a command, you must connect to a slave first:" +
                            "use the connect <ip_address> <port> command"
                        );
                        break;
                    }
                    
                    String[] result = executeCommand(slave, cmd, path);
                    System.out.println((result == null) ? "Command unsupported" : result[0]);
                    path = result[1];
                    
                    break;
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        
        String text = "some text";
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(text);
    }
}
