package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GomokuProtocol implements Protocol {

    public static final int SERVER_PORT_NUMBER = 9999;
    public static final int NUMBER_OF_PLAYERS = 2;
    private Status currentStatus = Status.WAITING_FOR_FIRST_CLIENT_CONNECTION;

    private static Logger loggerOfThisClass = Logger.getLogger(GomokuProtocol.class.getCanonicalName());
    private volatile Socket client1Socket;   // field filled with reflection
    private volatile Socket client2Socket;   // field filled with reflection
    private volatile Socket currentClientPlayerSocket = null;   // TODO : may be set as constructor parameter
    private Setup setup;

    @Override
    public Object processInput(@NotNull final Object input) throws IOException {

        return switch (currentStatus) {
            case WAITING_FOR_FIRST_CLIENT_CONNECTION -> waitingForFirstClientConnection(input);
            case WAITING_FOR_PARTIAL_SETUP -> setPartialSetup(input);
            case WAITING_FOR_SECOND_CLIENT_CONNECTION -> waitingForSecondClientConnection(input);
            case WAITING_FOR_COMPLETING_SETUP -> finalizeSetup(input);
            case SENDING_CURRENT_STATUS -> sendCurrentBoardToClients(input);
            case WAITING_FOR_MOVE_OF_A_CLIENT -> waitingForMoveOfAClientAndGet(input);
            case SENDING_SUMMARY -> sendingSummary(input);
            case CLOSING -> closing();
            default -> throw new IllegalStateException("Unexpected value: " + currentStatus);
        };
    }

    private Object closing() {
        // TODO: decide if invoking close() methods and how to divide responsibilities between protocolo and server gomoku instances and decide if current status should be updated to the first one or if close() method of this class should be invoked.
        currentStatus = Status.WAITING_FOR_MOVE_OF_A_CLIENT;
        return null;
    }

    public Object sendingSummary(Object input) throws IOException {
        // TODO : summary should come from the model
        Object tmp = sendCurrentBoardToClients(input);
        currentStatus = Status.CLOSING;
        return tmp;
    }

    synchronized public Object waitingForMoveOfAClientAndGet(Object ignored) throws IOException {
        // TODO : to be tested
        currentClientPlayerSocket = currentClientPlayerSocket == null ? client1Socket : client2Socket;
        ObjectInputStream ois = new ObjectInputStream(currentClientPlayerSocket.getInputStream());
        try {
            if (ois.readObject() instanceof Coordinates insertedCoord) {
                // TODO : send the just read move to the model
                currentClientPlayerSocket = currentClientPlayerSocket == client1Socket ? client2Socket : client1Socket;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();    // TODO : handle this exception
        }
        if (isGameEnded()) { // TODO : behaviour to be tested
            currentStatus = Status.SENDING_SUMMARY;
        } else {
            currentStatus = Status.SENDING_CURRENT_STATUS;
        }
        return null;
    }

    private boolean isGameEnded() {
        // TODO : ask to the model if the game is ended
        return true;
    }

    public Object sendCurrentBoardToClients(Object input) throws IOException {
        // TODO : board should come from the model
        if (input instanceof Board currentBoard) {
            AtomicReference<IOException> eventuallyThrownException = new AtomicReference<>();
            Arrays.stream(new Socket[]{client1Socket, client2Socket})
                    .forEach(aClient -> {
                        try {
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(aClient.getOutputStream());
                            oos.writeObject(currentBoard);
                            oos.flush();
                        } catch (IOException e) {
                            eventuallyThrownException.set(e);
                        }
                    });
            if (eventuallyThrownException.get() != null) {
                throw eventuallyThrownException.get();
            }
            currentStatus = Status.WAITING_FOR_MOVE_OF_A_CLIENT;
        } else {
            throw new IllegalArgumentException(// TODO : refactor needed (this is present almost in all methods)
                    "Expected " + Board.class.getCanonicalName() +
                            " as input parameter but received " + input.getClass());
        }
        return null;
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
        SENDING_CURRENT_STATUS,          // TODO: change to more significant status name
        WAITING_FOR_MOVE_OF_A_CLIENT,
        SENDING_SUMMARY,
        CLOSING
    }

}