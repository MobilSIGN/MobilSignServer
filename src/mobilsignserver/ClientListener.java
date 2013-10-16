/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class ClientListener  extends Thread {
    
    private BufferedReader mIn; //citac
    private Queue<String> mReceivedMessages;
    
    
    public ClientListener(Socket socket){                       
        try{
            mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));        
            mReceivedMessages = new LinkedBlockingQueue<>();            
        }
        catch(IOException ex){
            System.err.println("Vyskytla sa chyba pri vytvarani clientListenera");
        }
    }
 
    /**
     * Until interrupted, reads messages from the client socket, forwards them
     * to the server dispatcher's queue and notifies the server dispatcher.
     */
    @Override
    public void run()
    {
        try {
           while (!isInterrupted()) {
               String message = mIn.readLine();
               if (message != null){
                   mReceivedMessages.add(message);                   
               }              
           }
        } catch (IOException ioex) {
           // Problem reading from socket (communication is broken)
        }
    }
    
    public String getMessage(){
        return mReceivedMessages.poll();
    }
    
    public boolean hasMessage(){
        return (mReceivedMessages.peek() == null)?false:true;
    }
    
}
