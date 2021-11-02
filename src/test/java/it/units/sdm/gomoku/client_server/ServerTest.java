package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.utils.AccessWithReflectionException;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {

    static final String LOOPBACK_HOSTNAME = null;
    private Server server;
    private Thread serverThread;
    private final Logger testLogger = Logger.getLogger(getClass().getCanonicalName());
    private final static int SERVER_PORT_NUMBER = Server.SERVER_PORT_NUMBER;

    @BeforeEach
    void setUp() {
        try {
            Pair<Server, Thread> serverAndItsThread = createStartAndReturnServerAndItsThread();
            server = serverAndItsThread.getKey();
            serverThread = serverAndItsThread.getValue();
        } catch (IOException e) {
            testLogger.log(Level.SEVERE, "Unable to start server", e);
            fail(e);
        }
    }

    static Pair<Server,Thread> createStartAndReturnServerAndItsThread() throws IOException {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        return new Pair<>(server,serverThread);
    }

    static void shutdownAndCloseServer(@NotNull final Pair<Server, Thread> serverAndItsThread) {
        Server server = Objects.requireNonNull(Objects.requireNonNull(serverAndItsThread).getKey());
        Thread serverThread = Objects.requireNonNull(serverAndItsThread.getValue());
        server.close();
        serverThread.interrupt();
    }

    @AfterEach
    void tearDown() {
        shutdownAndCloseServer(new Pair<>(server, serverThread));
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