package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.utils.AccessWithReflectionException;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.units.sdm.gomoku.client_server.ClientServerUtility.LOOPBACK_HOSTNAME;
import static it.units.sdm.gomoku.client_server.ClientServerUtility.SERVER_PORT_NUMBER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ClientTest {

    private Client client;
    private Pair<GomokuServer, Thread> serverAndItsThread;
    private final Logger testLogger = Logger.getLogger(getClass().getCanonicalName());
    private boolean clientClosed = false;

    @BeforeEach
    void setUp() {
        try {
            serverAndItsThread = ClientServerUtility.createStartAndReturnServerAndItsThread();
        } catch (IOException e) {
            testLogger.log(Level.SEVERE, "Unable to start server", e);
            fail(e);
        }
        connectToServer();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (!clientClosed) {
            client.close();
        }
        ClientServerUtility.shutdownAndCloseServer(serverAndItsThread);
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