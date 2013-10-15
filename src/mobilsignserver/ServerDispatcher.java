/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.util.*;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class ServerDispatcher extends Thread {
    
    private Vector mMessageQueue = new Vector();
    private Vector mClients = new Vector();
 
    /**
     * Adds given client to the server's client list.
     */
    public synchronized void addClient(ClientInfo aClientInfo)
    {
        System.out.println("Client connected: " + aClientInfo.mSocket.getInetAddress().getHostAddress());
        mClients.add(aClientInfo);
    }
 
    /**
     * Deletes given client from the server's client list
     * if the client is in the list.
     */
    public synchronized void deleteClient(ClientInfo aClientInfo)
    {
        int clientIndex = mClients.indexOf(aClientInfo);
        if (clientIndex != -1) {
            if(aClientInfo.pair != null) {
                aClientInfo.pair.pair = null;
            }
            mClients.removeElementAt(clientIndex);
            System.out.println("Client disconnected: " + aClientInfo.mSocket.getInetAddress().getHostAddress());
        }
    }
 
    /**
     * Adds given message to the dispatcher's message queue and notifies this
     * thread to wake up the message queue reader (getNextMessageFromQueue method).
     * dispatchMessage method is called by other threads (ClientListener) when
     * a message is arrived.
     */
    public synchronized void dispatchMessage(ClientInfo aClientInfo, String aMessage)
    {
        System.out.println(aMessage);
        if (aMessage.length() > 5 && aMessage.substring(0, 5).equals("SEND:")) {
            if (aClientInfo.pair != null) {
                aClientInfo.pair.mClientSender.sendMessage(aMessage);
            } else {
                System.out.println("Client is not paired!");
            }
            
            return;
        }
        if (aMessage.length() > 5 && aMessage.substring(0, 5).equals("PAIR:")) {
            String fingerprint = aMessage.substring(5);
            ClientInfo pairClient = this.clientWithFingerprint(fingerprint);
            aClientInfo.fingerprint = fingerprint;
            aClientInfo.pair(pairClient);
            
            return;
        }
        System.out.println("Bad request! [" + aMessage + "]");
//        Socket socket = aClientInfo.mSocket;
//        String senderIP = socket.getInetAddress().getHostAddress();
//        String senderPort = "" + socket.getPort();
//        aMessage = senderIP + ":" + senderPort + " : " + aMessage;
//        mMessageQueue.add(aMessage);
//        notify();
    }
 
    /**
     * @return and deletes the next message from the message queue. If there is no
     * messages in the queue, falls in sleep until notified by dispatchMessage method.
     */
    private synchronized String getNextMessageFromQueue()
    throws InterruptedException
    {
        while (mMessageQueue.size()==0)
           wait();
        String message = (String) mMessageQueue.get(0);
        mMessageQueue.removeElementAt(0);
        return message;
    }
 
    /**
     * Sends given message to all clients in the client list. Actually the
     * message is added to the client sender thread's message queue and this
     * client sender thread is notified.
     */
    private synchronized void sendMessageToAllClients(String aMessage)
    {
        for (int i=0; i<mClients.size(); i++) {
           ClientInfo clientInfo = (ClientInfo) mClients.get(i);
           clientInfo.mClientSender.sendMessage(aMessage);
        }
    }
    
    private synchronized ClientInfo clientWithFingerprint(String fingerprint)
    {
        System.out.println("Clients: " + mClients.size());
        for (Object object : mClients) {
            ClientInfo client = (ClientInfo)object;
            if (client.fingerprint != null && client.fingerprint.equals(fingerprint)) {
                return client;
            }
        }
        
        return null;
    }
 
    /**
     * Infinitely reads messages from the queue and dispatch them
     * to all clients connected to the server.
     */
    public void run()
    {
        try {
           while (true) {
               String message = getNextMessageFromQueue();
               sendMessageToAllClients(message);
           }
        } catch (InterruptedException ie) {
           // Thread interrupted. Stop its execution
        }
    }
}
