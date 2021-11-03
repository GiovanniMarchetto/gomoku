package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GomokuProtocol implements Protocol {

    public static final int SERVER_PORT_NUMBER = 9999;
    public static final int NUMBER_OF_PLAYERS = 2;
    private Status currentStatus = Status.WAITING_FOR_FIRST_CLIENT_CONNECTION;

    private static Logger loggerOfThisClass = Logger.getLogger(GomokuProtocol.class.getCanonicalName());
    private Setup setup;

    @Override
    public Object processInput(@NotNull final Object input) throws IOException {

        return switch (currentStatus) {
            case WAITING_FOR_FIRST_CLIENT_CONNECTION -> waitingForFirstClientConnection(input);
            case WAITING_FOR_PARTIAL_SETUP -> setPartialSetup(input);
            case WAITING_FOR_SECOND_CLIENT_CONNECTION -> waitingForSecondClientConnection(input);
            default -> throw new IllegalStateException("Unexpected value: " + currentStatus);
        };
    }

    public Object waitingForSecondClientConnection(@NotNull final Object gomokuServer)
            throws IOException {
        makeTheServerToAcceptAClientAndSaveItsSocketInGivenFieldAndEventuallyUpdateCurrentStateIfNoError(
                gomokuServer, Status.WAITING_FOR_COMPLETING_SETUP, "client2Socket");
        return null;
    }


    @Nullable
    public Object waitingForFirstClientConnection(@NotNull Object gomokuServer)
            throws IOException {
        makeTheServerToAcceptAClientAndSaveItsSocketInGivenFieldAndEventuallyUpdateCurrentStateIfNoError(
                gomokuServer, Status.WAITING_FOR_PARTIAL_SETUP, "client1Socket");
        return null;
    }

    private void makeTheServerToAcceptAClientAndSaveItsSocketInGivenFieldAndEventuallyUpdateCurrentStateIfNoError(
            @NotNull final Object gomokuServer,
            @NotNull final Status nextStatusToUpdateIfNoError,
            @NotNull final String fieldNameWhereToSaveTheAcceptedClientSocketIfNoError)
            throws IOException {
        if (Objects.requireNonNull(gomokuServer) instanceof GomokuServer server) {
            try {
                Field fieldWhereToSaveAcceptedClientSocketIfNoError =
                        getClass().getDeclaredField(Objects.requireNonNull(fieldNameWhereToSaveTheAcceptedClientSocketIfNoError));
                fieldWhereToSaveAcceptedClientSocketIfNoError.setAccessible(true);
                fieldWhereToSaveAcceptedClientSocketIfNoError.set(this, server.acceptClientSocket());
                currentStatus = Objects.requireNonNull(nextStatusToUpdateIfNoError);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                loggerOfThisClass.log(Level.SEVERE, "Problems with reflections.", e);
            }
        } else {
            illegalStateNotification(gomokuServer);
        }
    }

    @Nullable
    public Object setPartialSetup(@NotNull Object partialSetup) {    // TODO : refactor (code duplication: very similar to previous method)
        if (Objects.requireNonNull(partialSetup) instanceof Setup partialSetupCasted) {
            this.setup = partialSetupCasted;
            this.currentStatus = Status.WAITING_FOR_SECOND_CLIENT_CONNECTION;
            return partialSetup;
        } else {
            illegalStateNotification(partialSetup);
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
        WAITING_FOR_FIRST_CLIENT_CONNECTION,
        WAITING_FOR_PARTIAL_SETUP,
        WAITING_FOR_SECOND_CLIENT_CONNECTION,
        WAITING_FOR_COMPLETING_SETUP
    }

}