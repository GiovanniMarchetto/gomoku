package it.units.sdm.gomoku.client_server.server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")   // Converting to record will weaken the visibility of private fields
public class ServiceRequest implements Runnable {

    private final Socket socketToClient;
    private final Protocol protocol;

    public ServiceRequest(@NotNull final Socket socketToClient, @NotNull final Protocol protocol) {
        this.socketToClient = Objects.requireNonNull(socketToClient);
        this.protocol = Objects.requireNonNull(protocol);
    }

    @Override
    public void run() {
        serviceOneClientRequest();
    }

    private void serviceOneClientRequest() {
        try (
                ObjectOutputStream outFromServerToClient =
                        new ObjectOutputStream(socketToClient.getOutputStream());
                ObjectInputStream inFromClientToServer =
                        new ObjectInputStream(socketToClient.getInputStream())
        ) {
            outFromServerToClient.writeObject(
                    protocol.processInput(inFromClientToServer.readObject()));
            outFromServerToClient.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();    // TODO : handle this exception
        }
    }
}
