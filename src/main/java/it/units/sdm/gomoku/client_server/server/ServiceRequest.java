package it.units.sdm.gomoku.client_server.server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

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
                PrintWriter outFromServerToClient =
                        new PrintWriter(socketToClient.getOutputStream(), true);
                BufferedReader inFromClientToServer =
                        new BufferedReader(
                                new InputStreamReader(socketToClient.getInputStream()))
        ) {
            String inputAsStr = inFromClientToServer.readLine();
            String outputAsStr = (String) protocol.processInput(inputAsStr);
            outFromServerToClient.println(outputAsStr);
        } catch (IOException e) {
            e.printStackTrace();    // TODO : handle this exception
        }
    }
}
