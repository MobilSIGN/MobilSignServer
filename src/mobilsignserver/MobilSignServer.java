package mobilsignserver;

import java.io.*;
import java.net.*;

public class MobilSignServer {

    //public static final String SERVER_HOSTNAME = "localhost";
    //public static final String SERVER_HOSTNAME = "10.0.1.3";
    public static final int LISTENING_PORT = 2002;
    private ServerSocket serverSocket;

    /**
     * Metoda odstartuje server
     *
     * @return void
     */
    public void start() {
        try {
            this.serverSocket = new ServerSocket(LISTENING_PORT);
        } catch (IOException ex) {
            System.err.println("Server sa nepodarilo spustit");
            return;
        }

        System.out.println("Server počúva na porte: " + LISTENING_PORT);

        //vytvorime obsluzneho manazera pre klientov
        ServerDispatcher serverDispatcher = new ServerDispatcher();
        serverDispatcher.start();

        //donekonecna
        while (true) {
            Socket socket;
            try {
                socket = this.serverSocket.accept();
            } catch (IOException ex) {
                System.err.println("Nepodarilo sa prijat nove spojenie");
                continue;
            }

            //vytvorime klienta
            ClientInfo clientInfo = new ClientInfo(socket);
            clientInfo.getClientListener().start();
            clientInfo.getClientSender().start();
            serverDispatcher.addClient(clientInfo);
            
            //prebudenie servera pokial bol prazdny a spal
            synchronized (serverDispatcher) {
                serverDispatcher.notify();
            }
        }
    }
}
