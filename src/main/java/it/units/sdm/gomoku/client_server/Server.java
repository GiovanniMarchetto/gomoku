package it.units.sdm.gomoku.client_server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Closeable, Runnable {

    public final static int SERVER_PORT_NUMBER = 9999;
    private final ServerSocket serverSocket;
    private final Logger serverLogger;

    public Server() throws IOException {
        this.serverLogger = Logger.getLogger(getClass().getCanonicalName());
        serverLogger.log(Level.INFO, "Server starting");
        this.serverSocket = new ServerSocket(SERVER_PORT_NUMBER);
        serverLogger.log(Level.INFO, "Server started");
    }

    @Override
    public void run() {
        serverLogger.log(Level.INFO, "Server ready");
        while (isServerRunning()) {
            serverLogger.log(Level.INFO,"Server waiting for a request from a client");
            try {
                serverSocket.accept();
            } catch (IOException ioe) {
                if (isServerRunning()) {
                    serverLogger.log(Level.SEVERE, "Error accepting connection", ioe);
                }
            }
        }
    }

    private boolean isServerRunning() {
        return !serverSocket.isClosed();
    }

    public void shutDown() {
        try {
            serverLogger.log(Level.INFO, "Server shutting down");
            serverSocket.close();
            serverLogger.log(Level.INFO, "Server shot down");
        } catch (IOException e) {
            serverLogger.log(Level.SEVERE, "Error in server shutdown", e);
        }
    }

    @Override
    public void close() {
        shutDown();
    }
}
