/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class ClientSender  extends Thread {
    
    private LinkedBlockingQueue mMessageQueue; 
    private PrintWriter mOut;
 
    public ClientSender(Socket socket){
       try{
        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        mMessageQueue = new LinkedBlockingQueue<>();
       }
       catch(IOException ex){
           System.err.println("Vyskytla sa chyba pri vytvarani clientSendera");
       }
    }
 
    /**
     * Adds given message to the message queue and notifies this thread
     * (actually getNextMessageFromQueue method) that a message is arrived.
     * sendMessage is called by other threads (ServeDispatcher).
     */
    public synchronized void sendMessage(String aMessage)
    {
        try{
            mMessageQueue.put(aMessage);
        }
        catch(InterruptedException ex){
            System.err.println("Vyskytla sa chyba pri vkladani spravy do frontu sprav na odoslanie");
        }
        notify();
    }
 
    /**
     * @return and deletes the next message from the message queue. If the queue
     * is empty, falls in sleep until notified for message arrival by sendMessage
     * method.
     */
    private synchronized String getNextMessageFromQueue() throws InterruptedException
    {
        while (mMessageQueue.isEmpty()){
           wait();
        }
        String message = (String) mMessageQueue.poll();
        return message;
    }
 
    /**
     * Sends given message to the client's socket.
     */
    private void sendMessageToClient(String aMessage)
    {
        mOut.println(aMessage);
        mOut.flush();
    }
 
    /**
     * Until interrupted, reads messages from the message queue
     * and sends them to the client's socket.
     */
    @Override
    public void run()
    {
        try {
           while (!isInterrupted()) {
               String message = getNextMessageFromQueue();
               sendMessageToClient(message);
           }
        } catch (Exception e) {
           // Commuication problem
        }       
    }
}
