/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.io.*;
import java.net.*;
//import javax.net.ssl.SSLServerSocket;
//import javax.net.ssl.SSLServerSocketFactory;
//import javax.net.ssl.SSLSocket;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class MobilSignServer {

    //public static final String SERVER_HOSTNAME = "localhost";
    //public static final String SERVER_HOSTNAME = "10.0.1.3";
    public static final int LISTENING_PORT = 2002;
 
    public static void main(String[] args)
    {
        // SSL
//        SSLServerSocketFactory factory=(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//        SSLServerSocket sslserversocket = null;
        
        // Open server socket for listening
        ServerSocket serverSocket = null;
        try {
           //sslserversocket = (SSLServerSocket)factory.createServerSocket(LISTENING_PORT);
            
           serverSocket = new ServerSocket(LISTENING_PORT);
           System.out.println("Server started on port " + LISTENING_PORT);
        } catch (IOException se) {
           System.err.println("Can not start listening on port " + LISTENING_PORT);
           se.printStackTrace();
           System.exit(-1);
        }
 
        // Start ServerDispatcher thread
        ServerDispatcher serverDispatcher = new ServerDispatcher();
        serverDispatcher.start();
 
        // Accept and handle client connections
        while (true) {
           try {
               //SSLSocket sslsocket = (SSLSocket)sslserversocket.accept();
               
               Socket socket = serverSocket.accept();
               ClientInfo clientInfo = new ClientInfo();
               clientInfo.mSocket = socket;
               ClientListener clientListener =
                   new ClientListener(clientInfo, serverDispatcher);
               ClientSender clientSender =
                   new ClientSender(clientInfo, serverDispatcher);
               clientInfo.mClientListener = clientListener;
               clientInfo.mClientSender = clientSender;
               clientListener.start();
               clientSender.start();
               serverDispatcher.addClient(clientInfo);
           } catch (IOException ioe) {
               ioe.printStackTrace();
           }
        }
    }
}
