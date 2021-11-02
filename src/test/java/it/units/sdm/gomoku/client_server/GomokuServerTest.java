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

class GomokuServerTest {

    private GomokuServer gomokuServer;
    private Thread serverThread;
    private final Logger testLogger =
            Logger.getLogger(getClass().getCanonicalName());

    @BeforeEach
    void setUp() {
        try {
            Pair<GomokuServer, Thread> serverAndItsThread = ClientServerUtility.createStartAndReturnServerAndItsThread();
            gomokuServer = serverAndItsThread.getKey();
            serverThread = serverAndItsThread.getValue();
        } catch (IOException e) {
            testLogger.log(Level.SEVERE, "Unable to start gomokuServer", e);
            fail(e);
        }
    }

    @AfterEach
    void tearDown() {
        ClientServerUtility.shutdownAndCloseServer(new Pair<>(gomokuServer, serverThread));
    }

    @Test
    void testClientConnection() {
        assertTrue(isServerAcceptingOneClientConnection());
    }

    @Test
    void shutDown() {
        gomokuServer.shutDown();
        try {
            assertTrue(isServerSocketClosed() && !isServerAcceptingOneClientConnection());
        } catch (AccessWithReflectionException e) {
            fail(e);
        }
    }

    @Test
    void close() {
        gomokuServer.close();
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
            Field serverSocketField = GomokuServer.class.getDeclaredField("serverSocket");
            serverSocketField.setAccessible(true);
            return ((ServerSocket) serverSocketField.get(gomokuServer)).isClosed();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            testLogger.log(Level.SEVERE, "Exception thrown in utility method", e);
            throw new AccessWithReflectionException();
        }
    }

}