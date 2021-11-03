package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.fake_objects.SimpleClient;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.client_server.GomokuProtocol.Status;
import static org.junit.jupiter.api.Assertions.*;

class GomokuProtocolTest {

    private GomokuServer gomokuServer;
    private Status currentStatus;
    private GomokuProtocol gomokuProtocol;

    private static Stream<Arguments> partialSetupSupplier() {
        final int MAX_NUMBER_OF_GAMES = 50;
        return Arrays.stream(BoardSizes.values())
                .map(BoardSizes::getBoardSize)
                .flatMap(boardSizeVal ->
                        IntStream.rangeClosed(1, MAX_NUMBER_OF_GAMES)
                                .mapToObj(i ->
                                        new Setup(
                                                new Player("Player1_" + boardSizeVal),
                                                null,//new Player("Player2_" + boardSizeVal),
                                                new PositiveInteger(i),
                                                boardSizeVal)))
                .map(Arguments::of);
    }

    private static Stream<Arguments> protocolStatusSupplier() {
        return Arrays.stream(Status.values()).map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        currentStatus = Status.values()[0];
        gomokuProtocol = new GomokuProtocol();
        try {
            gomokuServer = new GomokuServer();

        } catch (IOException e) {
            fail(e);
        }
    }

    @AfterEach
    void tearDown() {
        gomokuServer.close();
    }

    @Test
    void waitingForFirstClientConnection() {
        try {
            SimpleClient simpleClient = new SimpleClient();
            Field clientSocketToServerField = simpleClient.getClass().getDeclaredField("socketToServer");
            clientSocketToServerField.setAccessible(true);
            Socket clientSocketToServer = (Socket) clientSocketToServerField.get(simpleClient);

            Thread clientThread = new Thread(simpleClient);
            clientThread.start();

            gomokuProtocol.waitingForFirstClientConnection(gomokuServer);
            clientThread.join();

            Field handledClientSocketsField = gomokuServer.getClass().getDeclaredField("handledClientSockets");
            handledClientSocketsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Set<Socket> handledClientSockets = (Set<Socket>) handledClientSocketsField.get(gomokuServer);

            assertEquals(
                    handledClientSockets.toArray(new Socket[0])[0].getRemoteSocketAddress(),
                    clientSocketToServer.getLocalSocketAddress());

            simpleClient.close();
        } catch (IOException | IllegalStateException | NoSuchFieldException | IllegalAccessException | InterruptedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("partialSetupSupplier")
    void setPartialSetup(Setup partialSetup) {
        setCurrentProtocolStatus(Status.WAITING_FOR_PARTIAL_SETUP);
        try {
            gomokuProtocol.processInput(partialSetup);
            assertTrue(isPartialSetupSet());
        } catch (IOException e) {
            fail(e);
        }
    }

    private boolean isPartialSetupSet() {
        try {
            Field setupSavedInProtocolInstance = getFieldAlreadyMadeAccessible(gomokuProtocol.getClass(), "setup");
            return setupSavedInProtocolInstance.get(gomokuProtocol) != null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
            return false;
        }
    }

    @ParameterizedTest
    @MethodSource("protocolStatusSupplier")
    void checkProtocolStatusChangeAfterProcessInput(GomokuProtocol.Status currentStatus) {
        setCurrentProtocolStatus(currentStatus);
        switch (currentStatus) {
            case WAITING_FOR_FIRST_CLIENT_CONNECTION -> waitingForFirstClientConnection();
            case WAITING_FOR_PARTIAL_SETUP -> setPartialSetup(new Setup(new Player("p1"), new Player("p2"), new PositiveInteger(), BoardSizes.NORMAL.getBoardSize()));
            // default -> fail(new UnsupportedOperationException("Unhandled status \"" + currentStatus + "\"")); // TODO : handle protocol status update
        }
//        assertEquals(getNextProtocolStatusOrNullIfLast(currentStatus), getCurrentProtocolStatusOrNullIfExceptionThrown());  // TODO : handle protocol status update
    }

    @NotNull
    private Status getCurrentProtocolStatusOrNullIfExceptionThrown() {
        try {
            return (Status) getCurrentProtocolStatusField().get(gomokuProtocol);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
            return null;
        }
    }

    private void setCurrentProtocolStatus(@NotNull final Status newCurrentStatus) {
        try {
            getCurrentProtocolStatusField().set(gomokuProtocol, Objects.requireNonNull(newCurrentStatus));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @NotNull
    private Field getCurrentProtocolStatusField() throws NoSuchFieldException {
        return getFieldAlreadyMadeAccessible(gomokuProtocol.getClass(), "currentStatus");
    }

    @NotNull
    private Field getFieldAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                @NotNull final String fieldName)
            throws NoSuchFieldException {
        Field field = Objects.requireNonNull(clazz)
                .getDeclaredField(Objects.requireNonNull(fieldName));
        field.setAccessible(true);
        return field;
    }

    @Nullable
    private static Status getNextProtocolStatusOrNullIfLast(@NotNull final Status currentStatus) {
        Status[] allStatuses = Status.values();
        int currentStatusIndex =
                IntStream.range(0, allStatuses.length)
                        .sequential()
                        .filter(index -> allStatuses[index] == Objects.requireNonNull(currentStatus))
                        .findFirst()
                        .orElseThrow();
        if (isLastStatus(allStatuses, currentStatusIndex)) {
            return null;
        } else {
            return allStatuses[currentStatusIndex + 1];
        }
    }

    private static boolean isLastStatus(Status[] allStatuses, int currentStatusIndex) {
        return currentStatusIndex == allStatuses.length - 1;
    }

}