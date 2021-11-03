package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.ClientServerUtility;
import it.units.sdm.gomoku.client_server.interfaces.Client;

import java.io.IOException;
import java.net.Socket;

public class SimpleClient implements Client {    // TODO : to be tested

    private final Socket socketToServer;   // TODO : Client may be an Abstract class

    public SimpleClient() throws IOException {
        this.socketToServer = new Socket(
                ClientServerUtility.LOOPBACK_HOSTNAME,
                ClientServerUtility.SERVER_PORT_NUMBER);
    }

    @Override
    public void close() throws IOException {
        socketToServer.close();
    }

    @Override
    public void run() {

    }
}
