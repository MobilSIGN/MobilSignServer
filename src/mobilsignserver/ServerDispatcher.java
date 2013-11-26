package mobilsignserver;

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
                aClientInfo.setPairedClient(null);
                aClientInfo.getPairedClient().setPairedClient(null);
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
            return;
        }
        System.out.println(aMessage);
        if (aMessage.length() > 5 && aMessage.substring(0, 5).equals("SEND:")) {
            if (aClientInfo.getPairedClient() != null) {
                aClientInfo.getPairedClient().getClientSender().sendMessage(aMessage);
                System.out.println("Su sparovani a poslal som spravu");
            } else {
                System.out.println("Client is not paired!");
            }

            return;
        }
        if (aMessage.length() > 5 && aMessage.substring(0, 5).equals("PAIR:")) {
            System.out.println("Som dispatcher, prislo mi PAIR");
            String fingerprint = aMessage.substring(5);
            ClientInfo pairClient = this.clientWithFingerprint(fingerprint);
            aClientInfo.setFingerprint(fingerprint);
            aClientInfo.pair(pairClient);
            return;
        }
        System.out.println("Bad request! [" + aMessage + "]");
    }

    /**
     * Sends given message to all clients in the client list. Actually the
     * message is added to theServerDispatcher.java:116 client sender thread's
     * message queue and this client sender thread is notified.
     */
    private void sendMessageToAllClients(String aMessage) {
        for (ClientInfo client : mClients) {
            client.getClientSender().sendMessage(aMessage);
        }
    }

    private ClientInfo clientWithFingerprint(String fingerprint) {
        System.out.println("Clients: " + mClients.size());
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
                    if (client.getClientListener().hasMessage()) {
                        this.dispatchMessage(client);
                    }
                    wait(10);
                }
            }
        } catch (Exception e) {
            System.err.println("Dispatcher exception");
            e.printStackTrace();
            //TODO nepresiel wait();
            //TODO nepresiel wait(100);
        }
    }
}
