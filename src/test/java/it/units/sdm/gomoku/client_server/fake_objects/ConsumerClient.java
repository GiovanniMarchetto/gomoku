package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.ClientServerUtility;
import it.units.sdm.gomoku.client_server.interfaces.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumerClient implements Client { // TODO : to be tested

    private final Socket socketToServer;   // TODO : Client may be an Abstract class
    private Object receivedFromServer;

    public ConsumerClient(final int serverPortToConnect) throws IOException {
        this.socketToServer = new Socket(ClientServerUtility.LOOPBACK_HOSTNAME, serverPortToConnect);
    }

    public Object getReceivedFromServer() {
        return receivedFromServer;
    }

    public Socket getSocketToServer() {
        return socketToServer;
    }

    @Override
    public void close() throws IOException {
        socketToServer.close();
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(socketToServer.getInputStream());
            this.receivedFromServer = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "I/O Exception", e);
        }
    }
}
