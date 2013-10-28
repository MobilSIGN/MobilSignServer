/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.io.*;
import java.net.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class MobilSignServer {

    //public static final String SERVER_HOSTNAME = "localhost";
    //public static final String SERVER_HOSTNAME = "10.0.1.3";
    public static final int LISTENING_PORT = 2002;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "signKeyStore");
        System.setProperty("javax.net.ssl.keyStorePassword", "signproject123");

        // Open server socket for listening
        ServerSocket serversocket = null;

        try {
            SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(LISTENING_PORT);
            //serversocket = new ServerSocket(LISTENING_PORT);

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
                Socket socket = serversocket.accept();
                //Socket socket = serverSocket.accept();
                ClientInfo clientInfo = new ClientInfo(socket);
                clientInfo.getClientListener().start();
                clientInfo.getClientSender().start();
                serverDispatcher.addClient(clientInfo);
                synchronized (serverDispatcher) {
                    serverDispatcher.notify();
                }
            } catch (IOException ioe) {
                System.err.println("Nastala chyba pri spustani servera");
            }
        }
    }
}
