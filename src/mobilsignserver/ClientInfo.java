package mobilsignserver;

import java.net.Socket;
import communicator.*;

public class ClientInfo {
    
    private Socket mSocket = null;
    private Listener mClientListener = null;
    private Sender mClientSender = null;
    private String mFingerprint = null;
    private ClientInfo mPair = null;
    
    public ClientInfo(Socket socket){
       this.mSocket = socket;
       this.mClientListener = new Listener(mSocket);
       this.mClientSender =   new Sender(mSocket);
        System.out.println("Vytvaram clientInfo");
    }
    
    protected void setSocket(Socket socket){
        this.mSocket = socket;
    }
    
    protected void setClientListener(Listener listener){
        this.mClientListener = listener;
    }
    
    protected void setClientSender(Sender sender){
        this.mClientSender = sender;
    }
    
    protected void setPairedClient(ClientInfo pair){
        this.mPair = pair;
    }
    
    protected Socket getSocket(){
        return this.mSocket;
    }
    
    
    protected Listener getClientListener(){
        return this.mClientListener;
    }
    
    protected Sender getClientSender(){
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
    
    public boolean pair(ClientInfo client) {   
        if(client == null || this.mFingerprint == null || client.mFingerprint == null || this.mPair != null || client.mPair != null || !this.mFingerprint.equals(client.mFingerprint) || this == client) 
            return false;
        
        this.mPair = client;
        client.mPair = this;
        
        this.mClientSender.sendMessage("RESP:paired");
        client.getClientSender().sendMessage("RESP:paired");        
        return true;
    }
    
}
