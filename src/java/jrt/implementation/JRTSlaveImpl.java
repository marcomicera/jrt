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

package jrt.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import jrt.slave.JRTSlave;

/**
 *
 * @author Marco Micera, Leonardo Bernardi
 */
public class JRTSlaveImpl extends UnicastRemoteObject implements JRTSlave {
    // RMI registry
    private String ipAddr;
    private int port;
    
    private static final String SERVANT_NAME = "RemoteTerminal";
    
    /**
     * Constructor: create a JRC slave with the specified port
     * @param port the specified registry port
     * @throws RemoteException 
     */
    public JRTSlaveImpl(int port) throws RemoteException {
        // Retrieving IP address
        try {
            ipAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            System.out.println("Couldn't get node's IP address: terminating...");
            ipAddr = null;
            System.exit(0);
        }
        
        this.port = port;
        
        // Making node's methods available to the network thanks to RMI
        startRegistry();
        bindObject();
    }
    
    /**
     * Executes the command on the slave machine
     * @param cmd command to be executed
     * @return the command output
     */
    @Override
    public String executeCommand(String cmd) {
        System.out.println("Server has requested the " + cmd + " command. Replying...");        
        
        StringBuffer output = new StringBuffer();
        
        String[] splitted = cmd.split("\\s+");
        Process p;
        
        try {
            if(splitted[2].equals("cd") == true){
                p = Runtime.getRuntime().exec(
                    "cmd /c cd",
                    null,
                    new File("./" + splitted[3])
                );
                System.out.println("edefdefd");
            } 
            else{
                p = Runtime.getRuntime().exec(cmd);
                System.out.println(splitted[2]);
            }
                
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null)
                output.append(line + "\n");
        } 
        catch (Exception e){e.printStackTrace();}

        return output.toString();
    }
    
    /**
     * Starts the RMI registry
     * @throws RemoteException 
     */
    private void startRegistry() throws RemoteException {
        LocateRegistry.createRegistry(port);
    }
    
    /**
     * Binds the JRT client to its URL
     * @throws RemoteException 
     */
    private void bindObject() throws RemoteException {
        try {
            Naming.rebind(
                // symbolic name
                "//" + ipAddr + ":" + Integer.toString(port) + "/" + SERVANT_NAME,

                // stub
                this
            );
        } catch(MalformedURLException ex) {
            Logger.getLogger(JRTSlaveImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String toString() {
        return
            "[SLAVE INFOS]\nIP address: " + ipAddr +
            "\nPort: " + port
        ;
    }
    
    /**
     * Main method which will handle multiple command requests from
     * the master in a multi-threaded fashion
     * @param args there should be only one parameter, the registry port
     * @throws RemoteException 
     */
    public static void main(String[] args) throws RemoteException {
        // Checking command line input
        if(args.length != 1) {
            System.out.println("Usage: java jrt.implementation.JRTSlaveImpl <port>");
            return;
        }
        if(Long.parseLong(args[0]) > 65535 || Long.parseLong(args[0]) < 0) {
            System.out.println("Invalid port number");
            return;
        }
        
        JRTSlave slave = new JRTSlaveImpl(Integer.parseInt(args[0]));
        
        // Printing slave infos
        System.out.println(slave);
        System.out.println("\n[REQUESTS]");
    }
}
