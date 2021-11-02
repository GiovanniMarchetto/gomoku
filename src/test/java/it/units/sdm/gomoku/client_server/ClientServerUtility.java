package it.units.sdm.gomoku.client_server;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class ClientServerUtility {

    static final int SERVER_PORT_NUMBER = 9999;
    static final String LOOPBACK_HOSTNAME = null;

    static Pair<Server, Thread> createStartAndReturnServerAndItsThread() throws IOException {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        return new Pair<>(server, serverThread);
    }

    static void shutdownAndCloseServer(@NotNull final Pair<Server, Thread> serverAndItsThread) {
        Server server = Objects.requireNonNull(Objects.requireNonNull(serverAndItsThread).getKey());
        Thread serverThread = Objects.requireNonNull(serverAndItsThread.getValue());
        server.close();
        serverThread.interrupt();
    }
}
