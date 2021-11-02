package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.utils.AccessWithReflectionException;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private static final String LOOPBACK_HOSTNAME = ServerTest.LOOPBACK_HOSTNAME;
    private Client client;
    private Pair<Server,Thread> serverAndItsThread;
    private final Logger testLogger = Logger.getLogger(getClass().getCanonicalName());
    private final static int SERVER_PORT_NUMBER = Server.SERVER_PORT_NUMBER;
    private boolean clientClosed = false;

    @BeforeEach
    void setUp() {
        try {
            serverAndItsThread = ServerTest.createStartAndReturnServerAndItsThread();
        } catch (IOException e) {
            testLogger.log(Level.SEVERE, "Unable to start server", e);
            fail(e);
        }
        connectToServer();
    }

    @AfterEach
    void tearDown() throws IOException {
        if(!clientClosed) {
            client.close();
        }
        ServerTest.shutdownAndCloseServer(serverAndItsThread);
    }

    @Test
    void connectToServer() {
        try {
            client = new Client(LOOPBACK_HOSTNAME, SERVER_PORT_NUMBER);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void close() {
        try {
            client.close();
            clientClosed = true;
            assertTrue(isSocketToClientClosed());
        } catch (IOException | AccessWithReflectionException e) {
            fail(e);
        }
    }

    private boolean isSocketToClientClosed() throws AccessWithReflectionException {
        try {
            Field socketField = Client.class.getDeclaredField("socketToServer");
            socketField.setAccessible(true);
            return ((Socket) socketField.get(client)).isClosed();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            testLogger.log(Level.SEVERE, "Exception thrown in utility method", e);
            throw new AccessWithReflectionException();
        }
    }
}