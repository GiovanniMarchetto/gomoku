package it.units.sdm.gomoku.client_server;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class ClientServerUtility {

    static final int SERVER_PORT_NUMBER = 9999;
    static final String LOOPBACK_HOSTNAME = null;

    static Pair<GomokuServer, Thread> createStartAndReturnServerAndItsThread() throws IOException {
        GomokuServer gomokuServer = new GomokuServer();
        Thread serverThread = new Thread(gomokuServer);
        serverThread.start();
        return new Pair<>(gomokuServer, serverThread);
    }

    static void shutdownAndCloseServer(@NotNull final Pair<GomokuServer, Thread> serverAndItsThread) {
        GomokuServer gomokuServer = Objects.requireNonNull(Objects.requireNonNull(serverAndItsThread).getKey());
        Thread serverThread = Objects.requireNonNull(serverAndItsThread.getValue());
        gomokuServer.close();
        serverThread.interrupt();
    }
}
