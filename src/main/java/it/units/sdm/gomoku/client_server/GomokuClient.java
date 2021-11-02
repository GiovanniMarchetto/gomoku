package it.units.sdm.gomoku.client_server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;

public class GomokuClient implements Client {

    @NotNull
    private final Socket socketToServer;

    protected GomokuClient(@Nullable final String serverHostName, final int serverPortNumber)
            throws IOException {
        this.socketToServer = new Socket(serverHostName, serverPortNumber);
    }

    @Override
    public void close() throws IOException {
        socketToServer.close();
    }

    @Override
    public void run() {
    }
}