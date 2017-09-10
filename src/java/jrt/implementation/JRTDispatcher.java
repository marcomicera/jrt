/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrt.implementation;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jrt.slave.JRTSlave;

/**
 *
 * @author Marco Micera, Leonardo Bernardi
 */
public class JRTDispatcher extends UnicastRemoteObject implements JRTSlave {
    // RMI registry
    private String ipAddr;
    private int port;
    
    private static final String SERVANT_NAME = "RemoteTerminal";
    
    private Map<String, JRTSlaveImpl> slaves;
    
    /**
     * Constructor: create a JRC slave dispatcher with the specified port
     * @param port the specified registry port
     * @throws RemoteException 
     */
    public JRTDispatcher(int port) throws RemoteException {
        // Retrieving IP address
        try {
            ipAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            System.out.println("Couldn't get node's IP address: terminating...");
            ipAddr = null;
            System.exit(0);
        }
        
        this.port = port;
        
        slaves = new HashMap<String, JRTSlaveImpl>();
        
        // Making node's methods available to the network thanks to RMI
        startRegistry();
        bindObject();
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
    public String[] executeCommand(String cmd, String id) throws RemoteException {
        return executeCommand(cmd, id, "./");
    }

    @Override
    public String[] executeCommand(String cmd, String path, String id) throws RemoteException {
        if(!slaves.containsKey(id))
            slaves.put(id, new JRTSlaveImpl());
        
        return slaves.get(id).executeCommand(cmd, path);
    }
    
    @Override
    public String toString() {
        return
            "[DISPATCHER INFOS]" +
            "\nIP address: " + ipAddr +
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
            System.out.println("Usage: java jrt.implementation.JRTDispatcher <port>");
            return;
        }
        if(Long.parseLong(args[0]) > 65535 || Long.parseLong(args[0]) < 0) {
            System.out.println("Invalid port number");
            return;
        }
        
        JRTDispatcher disp = new JRTDispatcher(Integer.parseInt(args[0]));
        
        // Printing slave infos
        System.out.println(disp);
        System.out.println("\n[REQUESTS]");
    }
}
