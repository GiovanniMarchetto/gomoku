package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.ClientServerUtility;
import it.units.sdm.gomoku.client_server.interfaces.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThatAcceptsOnlyOneRequestAndSavesItInField implements Server {    // TODO : to be tested
    private final ServerSocket serverSocket;
    private volatile Socket acceptedClientSocket;

    public ServerThatAcceptsOnlyOneRequestAndSavesItInField() throws IOException {
        this.serverSocket = new ServerSocket(ClientServerUtility.SERVER_PORT_NUMBER);
        this.acceptedClientSocket = null;
    }

    public Socket getAcceptedClientSocketOrNullIfNoClientsConnected() {
        return acceptedClientSocket;
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public void run() {
        try {
            acceptedClientSocket = serverSocket.accept();
        } catch (IOException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "I/O Exception", e);
        }
    }
}
