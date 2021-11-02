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

import static it.units.sdm.gomoku.client_server.ClientServerUtility.LOOPBACK_HOSTNAME;
import static it.units.sdm.gomoku.client_server.ClientServerUtility.SERVER_PORT_NUMBER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {

    private Server server;
    private Thread serverThread;
    private final Logger testLogger =
            Logger.getLogger(getClass().getCanonicalName());

    @BeforeEach
    void setUp() {
        try {
            Pair<Server, Thread> serverAndItsThread = ClientServerUtility.createStartAndReturnServerAndItsThread();
            server = serverAndItsThread.getKey();
            serverThread = serverAndItsThread.getValue();
        } catch (IOException e) {
            testLogger.log(Level.SEVERE, "Unable to start server", e);
            fail(e);
        }
    }

    @AfterEach
    void tearDown() {
        ClientServerUtility.shutdownAndCloseServer(new Pair<>(server, serverThread));
    }

    @Test
    void testClientConnection() {
        assertTrue(isServerAcceptingOneClientConnection());
    }

    @Test
    void shutDown() {
        server.shutDown();
        try {
            assertTrue(isServerSocketClosed() && !isServerAcceptingOneClientConnection());
        } catch (AccessWithReflectionException e) {
            fail(e);
        }
    }

    @Test
    void close() {
        server.close();
        shutDown();
    }

    private boolean isServerAcceptingOneClientConnection() {
        try {
            new Socket(LOOPBACK_HOSTNAME, SERVER_PORT_NUMBER);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isServerSocketClosed() throws AccessWithReflectionException {
        try {
            Field serverSocketField = Server.class.getDeclaredField("serverSocket");
            serverSocketField.setAccessible(true);
            return ((ServerSocket) serverSocketField.get(server)).isClosed();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            testLogger.log(Level.SEVERE, "Exception thrown in utility method", e);
            throw new AccessWithReflectionException();
        }
    }

}