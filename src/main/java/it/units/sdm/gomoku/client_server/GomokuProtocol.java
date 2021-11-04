package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GomokuProtocol implements Protocol {

    public static final int SERVER_PORT_NUMBER = 9999;
    public static final int NUMBER_OF_PLAYERS = 2;
    private Status currentStatus = Status.WAITING_FOR_FIRST_CLIENT_CONNECTION;

    private static Logger loggerOfThisClass = Logger.getLogger(GomokuProtocol.class.getCanonicalName());
    private Socket client1Socket;   // field filled with reflection
    private Socket client2Socket;   // field filled with reflection
    private Setup setup;

    @Override
    public Object processInput(@NotNull final Object input) throws IOException {

        return switch (currentStatus) {
            case WAITING_FOR_FIRST_CLIENT_CONNECTION -> waitingForFirstClientConnection(input);
            case WAITING_FOR_PARTIAL_SETUP -> setPartialSetup(input);
            case WAITING_FOR_SECOND_CLIENT_CONNECTION -> waitingForSecondClientConnection(input);
            case WAITING_FOR_COMPLETING_SETUP -> finalizeSetup(input);
            default -> throw new IllegalStateException("Unexpected value: " + currentStatus);
        };
    }

    @Nullable
    public Object waitingForFirstClientConnection(@NotNull Object gomokuServer)
            throws IOException {
        makeTheServerToAcceptAClientAndSaveItsSocketInGivenFieldAndEventuallyUpdateCurrentStateIfNoError(
                gomokuServer, Status.WAITING_FOR_PARTIAL_SETUP, "client1Socket");
        return null;
    }

    public Object waitingForSecondClientConnection(@NotNull final Object gomokuServer)
            throws IOException {
        makeTheServerToAcceptAClientAndSaveItsSocketInGivenFieldAndEventuallyUpdateCurrentStateIfNoError(
                gomokuServer, Status.WAITING_FOR_COMPLETING_SETUP, "client2Socket");
        return null;
    }

    @Nullable
    public Object setPartialSetup(@NotNull final Object partialSetup) {
        return setSetupIfValidAndUpdateProtocolStatusAndGetOrNullIfInvalid(
                partialSetup, Status.WAITING_FOR_SECOND_CLIENT_CONNECTION, false);
    }

    private Object finalizeSetup(@NotNull final Object finalizedSetup) {
        return setSetupIfValidAndUpdateProtocolStatusAndGetOrNullIfInvalid(
                finalizedSetup, Status.SENDING_CURRENT_STATUS, true);
    }

    @Nullable
    Object setSetupIfValidAndUpdateProtocolStatusAndGetOrNullIfInvalid(
            @NotNull final Object setup,
            @NotNull final Status newProtocolStatusIfNoErrors,
            final boolean trueIfGivenSetupMustBeFinalizedOrFalseIfSecondPlayerMustNotBeSpecified)
            throws IllegalArgumentException {
        // TODO : all this accesses via reflection are needed?
        // TODO : method to be tested (test correct behaviour of inner if statements)
        if (Objects.requireNonNull(setup) instanceof Setup castedSetup) {
            if (trueIfGivenSetupMustBeFinalizedOrFalseIfSecondPlayerMustNotBeSpecified) {
                if (!isFinalizedSetup(castedSetup)) {
                    throw new IllegalArgumentException("Setup object not completely finalized but should be");
                }
            } else {
                if (!isPartialSetup(castedSetup)) {
                    throw new IllegalArgumentException("Field \"player2\"" +
                            " in given setup object is not null but should be");
                }
            }
            this.setup = castedSetup;
            this.currentStatus = Objects.requireNonNull(newProtocolStatusIfNoErrors);
            return setup;
        } else {
            illegalStateNotification(setup);
        }
        return null;
    }

    public boolean isPartialSetup(@NotNull final Setup setup) {
        return Objects.requireNonNull(setup).player2() == null;
    }

    public boolean isFinalizedSetup(@NotNull final Setup setup) {
        return Arrays.stream(setup.getClass().getDeclaredFields())
                .unordered().parallel()
                .peek(aField -> aField.setAccessible(true))
                .map(aField -> getFieldValueOfSetupInstance(aField, Objects.requireNonNull(setup)))
                .noneMatch(Objects::isNull);
    }

    @Nullable
    private Object getFieldValueOfSetupInstance(
            @NotNull final Field field, @NotNull final Setup setup) {
        try {
            return Objects.requireNonNull(field)
                    .get(Objects.requireNonNull(setup));
        } catch (IllegalAccessException e) {
            loggerOfThisClass.log(Level.SEVERE, "Illegal access via reflection", e);
            return null;
        }
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
        WAITING_FOR_COMPLETING_SETUP,
        SENDING_CURRENT_STATUS          // TODO: change to more significant status name
    }

}