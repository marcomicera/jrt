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
public class JRTSlaveImpl {
    public JRTSlaveImpl() {
        
    }
    
    /**
     * Executes the command on the slave machine
     * @param cmd command to be executed
     * @return the command output
     */
    public String[] executeCommand(String cmd, String path) {
        System.out.println("Server has requested the " + cmd + " command. Replying...");        
        
        StringBuffer output = new StringBuffer();

        Process p;
        String nextPath = "";
        try {
            p = Runtime.getRuntime().exec(cmd + " && cd", null, new File(path));
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            
            while ((line = reader.readLine())!= null){
                output.append(line + "\n");
                nextPath = line;
            }
        } 
        catch (Exception e){e.printStackTrace();}
        
        String[] splitted = output.toString().split("\\n+");
        output.delete(0, output.length());
        
        for(int i=0; i < splitted.length - 1 ;i++)
            output.append(splitted[i] + "\n");
                
        
        String[] arrayOutput = new String[2];
        arrayOutput[0] = output.toString();
        arrayOutput[1] = nextPath;
        
        return arrayOutput;
    }
    
    public String[] executeCommand(String cmd) {
        return executeCommand(cmd,"./");
    }
}
