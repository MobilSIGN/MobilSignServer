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
    
    public Socket mSocket = null;
    public ClientListener mClientListener = null;
    public ClientSender mClientSender = null;
    public String fingerprint = null;
    public ClientInfo pair = null;
    
    public boolean pair(ClientInfo client)
    {   
        //System.out.println("Pairing: " + client.pair.mSocket.getInetAddress().getHostAddress() + " <-> " + client.mSocket.getInetAddress().getHostAddress());
        if(client == null || this.fingerprint == null || client.fingerprint == null || this.pair != null || client.pair != null || !this.fingerprint.equals(client.fingerprint) || this == client) 
            return false;
        
        this.pair = client;
        client.pair = this;
        
        System.out.println("Clients paired: " + client.pair.mSocket.getInetAddress().getHostAddress() + " <-> " + client.mSocket.getInetAddress().getHostAddress());
        this.mClientSender.sendMessage("RESP:paired");
        client.mClientSender.sendMessage("RESP:paired");
        
        return true;
    }
    
}
