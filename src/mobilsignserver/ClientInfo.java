/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobilsignserver;

import java.net.Socket;

/**
 *
 * @author Marek Spalek <marekspalek@gmail.com>
 */
public class ClientInfo {
    
    private Socket mSocket = null;
    private ClientListener mClientListener = null;
    private ClientSender mClientSender = null;
    private String mFingerprint = null;
    private ClientInfo mPair = null;
    
    public ClientInfo(Socket socket){
       this.mSocket = socket;
       this.mClientListener = new ClientListener(mSocket);
       this.mClientSender =   new ClientSender(mSocket);
       
    }
    
    protected void setSocket(Socket socket){
        this.mSocket = socket;
    }
    
    protected void setClientListener(ClientListener listener){
        this.mClientListener = listener;
    }
    
    protected void setClientSender(ClientSender sender){
        this.mClientSender = sender;
    }
    
    protected void setPairedClient(ClientInfo pair){
        this.mPair = pair;
    }
    
    protected Socket getSocket(){
        return this.mSocket;
    }
    
    
    protected ClientListener getClientListener(){
        return this.mClientListener;
    }
    
    protected ClientSender getClientSender(){
        return this.mClientSender;
    }
    
    protected ClientInfo getPairedClient(){
        return this.mPair;
    }
    
    protected void setFingerprint(String fingerprint) {
        this.mFingerprint = fingerprint;
    }
    
    protected String getFingerprint() {
        return mFingerprint;
    }
    
    public boolean pair(ClientInfo client)
    {   
        //System.out.println("Pairing: " + client.pair.mSocket.getInetAddress().getHostAddress() + " <-> " + client.mSocket.getInetAddress().getHostAddress());
        if(client == null || this.mFingerprint == null || client.mFingerprint == null || this.mPair != null || client.mPair != null || !this.mFingerprint.equals(client.mFingerprint) || this == client) 
            return false;
        
        this.mPair = client;
        client.mPair = this;
        
        System.out.println("Clients paired: " + client.mPair.mSocket.getInetAddress().getHostAddress() + " <-> " + client.mSocket.getInetAddress().getHostAddress());
        this.mClientSender.sendMessage("RESP:paired");
        client.getClientSender().sendMessage("RESP:paired");        
        return true;
    }
    
}
