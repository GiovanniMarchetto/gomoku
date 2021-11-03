package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GomokuProtocol implements Protocol {

    public static final int SERVER_PORT_NUMBER = 9999;
    public static final int NUMBER_OF_PLAYERS = 2;
    private final Status currentStatus = Status.WAITING_FOR_FIRST_CLIENT_CONNECTION;

    @Override
    public Object processInput(@NotNull final Object input) throws IOException {

        return switch (currentStatus) {
            case WAITING_FOR_FIRST_CLIENT_CONNECTION -> waitingForFirstClientConnection(input);
            default -> throw new IllegalStateException("Unexpected value: " + currentStatus);
        };
    }

    @Nullable
    public Object waitingForFirstClientConnection(@NotNull Object input)
            throws IOException, IllegalStateException {
        if (input instanceof GomokuServer gomokuServer) {
            gomokuServer.acceptClientSocket();
        } else {
            illegalStateNotification(input);
        }
        return null;
    }

    private void illegalStateNotification(@NotNull Object input)
            throws IllegalStateException {
        throw new IllegalStateException("Current state is " + currentStatus + ".\n"
                + "Expected input type " + GomokuServer.class.getCanonicalName()
                + " but received " + input.getClass().getCanonicalName());
    }


    public enum Status {
        WAITING_FOR_FIRST_CLIENT_CONNECTION
    }

}