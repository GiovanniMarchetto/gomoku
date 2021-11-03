package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GomokuServer implements Server { // TODO : add more specific tests?

    public final static int SERVER_PORT_NUMBER = 9999;
    private final static int NUMBER_OF_PROCESSABLE_CONCURRENT_REQUESTS = GameProtocol.NUMBER_OF_PLAYERS;
    private final Logger serverLogger;
    private final ServerSocket serverSocket;
    private final Set<Socket> handledClientSockets;
    private final ExecutorService serviceRequestsOfClientsExecutorService;
    private final Thread clientsHandlerThread;

    public GomokuServer() throws IOException {
        this.serverLogger = Logger.getLogger(getClass().getCanonicalName());
        this.serverLogger.log(Level.INFO, "Server starting");
        this.serverSocket = new ServerSocket(SERVER_PORT_NUMBER);
        this.serviceRequestsOfClientsExecutorService =
                Executors.newFixedThreadPool(NUMBER_OF_PROCESSABLE_CONCURRENT_REQUESTS);
        this.handledClientSockets = ConcurrentHashMap.newKeySet();
        this.clientsHandlerThread = new Thread(
                new ClientsHandler(
                        handledClientSockets,
                        new GameProtocol(),
                        serviceRequestsOfClientsExecutorService));
        this.clientsHandlerThread.start();
        this.serverLogger.log(Level.INFO, "Server started");
    }

    @Override
    public void run() {
        serverLogger.log(Level.INFO, "Server ready");
        while (isServerRunning()) {
            serverLogger.log(Level.INFO, "Server waiting for a request from a client");
            try {
                handledClientSockets.add(serverSocket.accept());
            } catch (IOException ioe) {
                if (isServerRunning()) {
                    serverLogger.log(Level.SEVERE, "Error accepting connection", ioe);
                }
            }
        }
    }

    public boolean isServerRunning() {
        return !serverSocket.isClosed();
    }

    public void shutDown() {
        try {
            serverLogger.log(Level.INFO, "Server shutting down");
            serverSocket.close();
            serviceRequestsOfClientsExecutorService.shutdownNow();
            try {
                //Stop accepting requests.
                serverSocket.close();
                clientsHandlerThread.join();
                serverLogger.log(Level.INFO, "SERVER closed");
            } catch (InterruptedException ignored) {
                // already interrupted
            } catch (IOException e) {
                System.err.println("[SERVER] Error in server shutdown");
                e.printStackTrace();
            }
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
