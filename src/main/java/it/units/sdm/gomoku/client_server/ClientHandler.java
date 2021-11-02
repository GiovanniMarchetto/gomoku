package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ClientHandler implements Runnable {

    private final Set<Socket> handledClientSockets;
    private final Protocol protocol;
    private final ExecutorService serviceRequestsOfClientsExecutorService;

    public ClientHandler(@NotNull final Set<Socket> handledClientSockets,
                         @NotNull final Protocol protocol,
                         @NotNull final ExecutorService serviceRequestsOfClientsExecutorService) {
        this.handledClientSockets = Objects.requireNonNull(handledClientSockets);
        this.protocol = Objects.requireNonNull(protocol);
        this.serviceRequestsOfClientsExecutorService =
                Objects.requireNonNull(serviceRequestsOfClientsExecutorService);
    }

    @Override
    public void run() {
        while (isExecutorServiceRunning()) {
            Iterator<Socket> socketIterator = handledClientSockets.iterator();
            while (socketIterator.hasNext()) {
                Socket socket = socketIterator.next();
                if (socket.isClosed()) {
                    socketIterator.remove();
                } else {
                    try {
                        if (socket.getInputStream().available() != 0) {
                            serviceRequestsOfClientsExecutorService
                                    .submit(new ServiceRequest(socket, protocol));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isExecutorServiceRunning() {
        return !serviceRequestsOfClientsExecutorService.isShutdown();
    }
}
