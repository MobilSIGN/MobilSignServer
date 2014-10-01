package mobilsignserver;

import communicator.Util;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerDispatcher extends Thread {

    private CopyOnWriteArrayList<ClientInfo> mClients;

    public ServerDispatcher() {
        mClients = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds given client to the server's client list.
     */
    public void addClient(ClientInfo aClientInfo) {
        mClients.add(aClientInfo);
    }

    /**
     * Deletes given client from the server's client list if the client is in
     * the list.
     */
    public void deleteClient(ClientInfo aClientInfo) {
        if (mClients.contains(aClientInfo)) { //ak sa klient nachadza na serveri
            if (aClientInfo.getPairedClient() != null) {
                aClientInfo.getPairedClient().getClientSender().putMesssageToQueue("RESP:unpaired");
                aClientInfo.getPairedClient().setPairedClient(null);
                aClientInfo.setPairedClient(null);
            }
            mClients.remove(aClientInfo);
        }
    }

    /**
     * Adds given message to the dispatcher's message queue and notifies this
     * thread to wake up the message queue reader (getNextMessageFromQueue
     * method). dispatchMessage method is called by other threads
     * (ClientListener) when a message is arrived.
     */
    public void dispatchMessage(ClientInfo aClientInfo) {
        String aMessage = aClientInfo.getClientListener().getMessage();
        if (aMessage == null) { //dodatocna kontrola
            System.err.println("Prisla NULL sprava");
            return;
        }

        //pokial sprava obsahuje len typ a nic viac(malo znakov)
        if (aMessage.length() <= 5) {
            System.err.println("Prazdna sprava iba s typom");
            return;
        }

        //v zavislosti od typu spravy
        switch (this.getMessageType(aMessage)) {
            case Util.TYPE_SEND: //send sprava
            case Util.TYPE_MPUB: //mobile public key
                if (aClientInfo.getPairedClient() != null) {
                    aClientInfo.getPairedClient().getClientSender().putMesssageToQueue(aMessage);
                } else {
                    System.err.println("Client is not paired!");
                }
                break;

            case Util.TYPE_PAIR: //pair request                
                String fingerprint = aMessage.substring(5);
                ClientInfo pairClient = this.clientWithFingerprint(fingerprint);
                aClientInfo.setFingerprint(fingerprint);
                aClientInfo.pair(pairClient);
                break;

            default:
                System.err.println("Neimplementovany typ spravy");
                break;
        }
    }

    /**
     * Metoda vrati typ spravy
     *
     * @param message - cela sprava, ktorej typ ideme zistit
     * @return string reprezentacia typu spravy
     */
    private String getMessageType(String message) {
        return message.substring(0, 5);
    }

    /**
     * Sends given message to all clients in the client list. Actually the
     * message is added to theServerDispatcher.java:116 client sender thread's
     * message queue and this client sender thread is notified.
     */
    private void sendMessageToAllClients(String aMessage) {
        for (ClientInfo client : mClients) {
            client.getClientSender().putMesssageToQueue(aMessage);
        }
    }

    private ClientInfo clientWithFingerprint(String fingerprint) {
        for (Object object : mClients) {
            ClientInfo client = (ClientInfo) object;
            if (client.getFingerprint() != null && client.getFingerprint().equals(fingerprint)) {
                return client;
            }
        }

        return null;
    }

    /**
     * Infinitely reads messages from the queue and dispatch them to all clients
     * connected to the server.
     */
    @Override
    public synchronized void run() {
        try {
            while (true) {
                if (mClients.isEmpty()) {
                    wait();
                }
                for (ClientInfo client : mClients) {
                    if (client.getClientListener().isAlive()) {
                        if (client.getClientListener().hasMessage()) {
                            this.dispatchMessage(client);
                        }
                    } else {
                        deleteClient(client);
                    }
                    wait(10);
                }
            }
        } catch (Exception e) {
            System.err.println("Dispatcher exception");                        
        }
    }
}
