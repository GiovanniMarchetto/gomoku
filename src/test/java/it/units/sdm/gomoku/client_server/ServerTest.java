package it.units.sdm.gomoku.client_server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {

    private static final String LOOPBACK_HOSTNAME = null;
    private Server server;
    private final Logger testLogger = Logger.getLogger(getClass().getCanonicalName());
    private final static int SERVER_PORT_NUMBER = Server.SERVER_PORT_NUMBER;

    @BeforeEach
    void setUp() {
        try {
            server = new Server();
            new Thread(server).start();
        } catch (IOException e) {
            fail(e);
        }
    }

    @AfterEach
    void tearDown() {
        server.shutDown();
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

    @Test
    void close() {
        server.close();
        shutDown();
    }

    private static class AccessWithReflectionException extends Throwable {
    }
}