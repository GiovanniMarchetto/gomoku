package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.client_server.fake_objects.ConsumerClient;
import it.units.sdm.gomoku.client_server.fake_objects.SimpleClient;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.client_server.GomokuProtocol.Status;
import static org.junit.jupiter.api.Assertions.*;

class GomokuProtocolTest {

    private GomokuServer gomokuServer;
    private Status currentStatus;
    private GomokuProtocol gomokuProtocol;

    private static Stream<Arguments> setupSupplierIndicatingIfIsPartialSetupInSecondArgument() {
        final int MAX_NUMBER_OF_GAMES = 50;
        return Arrays.stream(BoardSizes.values())
                .map(BoardSizes::getBoardSize)
                .flatMap(boardSizeVal ->
                        IntStream.rangeClosed(1, MAX_NUMBER_OF_GAMES)
                                .flatMap(i -> IntStream.rangeClosed(1, 2).map(j -> i * j))
                                .mapToObj(i -> {
                                    boolean trueIfIsPartialSetup = i % 2 == 0;
                                    return Arguments.of(
                                            new Setup(
                                                    new Player("Player1_" + boardSizeVal),
                                                    trueIfIsPartialSetup
                                                            ? null
                                                            : new Player("Player2_" + boardSizeVal),
                                                    new PositiveInteger(i),
                                                    boardSizeVal),
                                            trueIfIsPartialSetup);
                                }));
    }

    private static Stream<Arguments> protocolStatusSupplier() {
        return Arrays.stream(Status.values()).map(Arguments::of);
    }

    public static Stream<Arguments> boardSupplier() {
        final int NUMBER_OF_ARGUMENTS = 100;
        return IntStream.range(0, NUMBER_OF_ARGUMENTS)
                .mapToObj(j -> {
                    Board board = new Board(EnvVariables.BOARD_SIZE);
                    CPUPlayer cpuPlayer = new CPUPlayer();
                    final int MAX_NUMBER_OF_STONE_TO_PLACE_ON_THE_BOARD = 10;
                    for (int i = 0; i < MAX_NUMBER_OF_STONE_TO_PLACE_ON_THE_BOARD; i++) {
                        try {
                            cpuPlayer.chooseRandomEmptyCoordinates(board);
                        } catch (Board.NoMoreEmptyPositionAvailableException ignored) {
                        }
                    }
                    return board;
                })
                .map(Arguments::of);
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
        assertCorrectNumberOfClientsConnectedInGivenProtocolState(1, Status.WAITING_FOR_FIRST_CLIENT_CONNECTION);
    }

    @Test
    void waitingForSecondClientConnection() {
        waitingForFirstClientConnection();  // simulate the previous Status
        assertCorrectNumberOfClientsConnectedInGivenProtocolState(2, Status.WAITING_FOR_SECOND_CLIENT_CONNECTION);
    }

    @ParameterizedTest
    @MethodSource("boardSupplier")
    void sendCurrentGameStatusToUsersWhenInTheCorrectStatus(Board board) {
        // TODO : refactor needed
        AtomicReference<Exception> eventuallyThrownException = new AtomicReference<>();
        final int FAKE_SERVER_PORT = 20000;
        try (ServerSocket fakeServerAccepting2Clients = new ServerSocket(FAKE_SERVER_PORT);) {
            Thread fakeServerThread = new Thread(() -> {
                for (int numberOfAcceptedClients = 0; numberOfAcceptedClients < 2; numberOfAcceptedClients++) {
                    try {
                        getFieldAlreadyMadeAccessible(gomokuProtocol.getClass(),
                                "client" + (numberOfAcceptedClients + 1) + "Socket")
                                .set(gomokuProtocol, fakeServerAccepting2Clients.accept());
                    } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                        eventuallyThrownException.set(e);
                        return;
                    }
                }
                setCurrentProtocolStatus(Status.SENDING_CURRENT_STATUS);
                try {
                    gomokuProtocol.processInput(board);
                } catch (IOException e) {
                    eventuallyThrownException.set(e);
                    return;
                }
            });
            fakeServerThread.start();
            ConsumerClient c1 = new ConsumerClient(FAKE_SERVER_PORT);
            ConsumerClient c2 = new ConsumerClient(FAKE_SERVER_PORT);

            Thread t1 = new Thread(c1);
            Thread t2 = new Thread(c2);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            fakeServerThread.join();

            Arrays.stream(new Object[]{c1.getReceivedFromServer(), c2.getReceivedFromServer()})
                    .forEach(whatAClientReceived -> {
                        if (whatAClientReceived instanceof Board boardReceivedByAClient) {
                            assertEquals(board, boardReceivedByAClient);
                        } else {
                            fail(new IllegalArgumentException("Received object is not an instance of class " +
                                    board.getClass().getCanonicalName()));
                        }
                    });
            if (eventuallyThrownException.get() != null) {
                fail(eventuallyThrownException.get());
            }
        } catch (Exception e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("setupSupplierIndicatingIfIsPartialSetupInSecondArgument")
    void setPartialSetup(Setup partialSetup, boolean isPartialSetup) {
        testIfPartialOfFullSetupSuccessfulCompleted(partialSetup, isPartialSetup, Status.WAITING_FOR_PARTIAL_SETUP);
    }

    @ParameterizedTest
    @MethodSource("setupSupplierIndicatingIfIsPartialSetupInSecondArgument")
    void setFullSetup(Setup fullSetup, boolean isPartialSetup) {
        testIfPartialOfFullSetupSuccessfulCompleted(fullSetup, isPartialSetup, Status.WAITING_FOR_COMPLETING_SETUP);
    }

    private void testIfPartialOfFullSetupSuccessfulCompleted(
            @NotNull final Setup partialOrFullSetup, boolean partialSetupProvided, @NotNull Status currentStatus) {
        setCurrentProtocolStatus(Objects.requireNonNull(currentStatus));    // TODO: refactor needed
        try {
            boolean partialSetupDesired = currentStatus == Status.WAITING_FOR_PARTIAL_SETUP;
            try {
                gomokuProtocol.processInput(Objects.requireNonNull(partialOrFullSetup));
            } catch (IllegalArgumentException e) {
                if (partialSetupDesired == partialSetupProvided) {
                    fail(e);
                } else {
                    return; // exception thrown as expected
                }
            }
            assertEquals(partialSetupProvided, isPartialSetup());
        } catch (IOException e) {
            fail(e);
        }
    }

    private boolean isPartialSetup() {
        try {
            Field setupFieldSavedInProtocolInstance = getFieldAlreadyMadeAccessible(gomokuProtocol.getClass(), "setup");
            if (setupFieldSavedInProtocolInstance.get(gomokuProtocol) instanceof Setup castedSetup) {
                return gomokuProtocol.isPartialSetup(castedSetup)
                        && !gomokuProtocol.isFinalizedSetup(castedSetup);
            } else {
                throw new IllegalArgumentException("Not an instance of " + Setup.class.getCanonicalName());
            }
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
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
            case WAITING_FOR_PARTIAL_SETUP -> setPartialSetup(new Setup(new Player("p1"), null, new PositiveInteger(), BoardSizes.NORMAL.getBoardSize()), true);
            // default -> fail(new UnsupportedOperationException("Unhandled status \"" + currentStatus + "\"")); // TODO : handle protocol status update
        }
//        assertEquals(getNextProtocolStatusOrNullIfLast(currentStatus), getCurrentProtocolStatusOrNullIfExceptionThrown());  // TODO : handle protocol status update
    }

    private void assertCorrectNumberOfClientsConnectedInGivenProtocolState(
            final int EXPECTED_HANDLED_CLIENTS, @NotNull final Status currentStatus) {
        setCurrentProtocolStatus(currentStatus);
        assertCorrectNumberOfClientsConnectedToTheServer(EXPECTED_HANDLED_CLIENTS);
    }

    private void assertCorrectNumberOfClientsConnectedToTheServer(final int numberOfClientsToBeConnectedToTheServer) {
        try {
            SimpleClient simpleClient = makeAClientToConnectToTheServerAndGet();
            assertEquals(numberOfClientsToBeConnectedToTheServer, getRemoteAddressOfClientSocketsHandledByTheServer().size());  // TODO : double assertions in sngle test
            assertTrue(isClientLocalSocketAddressHandledByServer(getLocalSocketAddressOf(simpleClient)));
            simpleClient.close();
        } catch (IOException | IllegalStateException | NoSuchFieldException | IllegalAccessException | InterruptedException e) {
            fail(e);
        }
    }

    @NotNull
    private SimpleClient makeAClientToConnectToTheServerAndGet() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        SimpleClient simpleClient = new SimpleClient();

        Thread clientThread = new Thread(simpleClient);
        clientThread.start();

        gomokuProtocol.waitingForFirstClientConnection(gomokuServer);
        clientThread.join();

        return simpleClient;
    }

    @NotNull
    private SocketAddress getLocalSocketAddressOf(@NotNull final SimpleClient simpleClient) {
        // TODO : SimpleClient may extends abstract class Client which has field "socketToServer"
        try {
            Field clientSocketToServerField = simpleClient.getClass().getDeclaredField("socketToServer");
            clientSocketToServerField.setAccessible(true);
            Socket clientSocketToServer = (Socket) clientSocketToServerField.get(simpleClient);
            return clientSocketToServer.getLocalSocketAddress();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
            return null;
        }
    }

    private boolean isClientLocalSocketAddressHandledByServer(@NotNull final SocketAddress clientLocalSocketAddress) {
        return getRemoteAddressOfClientSocketsHandledByTheServer()
                .contains(Objects.requireNonNull(clientLocalSocketAddress));
    }

    @NotNull
    private Set<SocketAddress> getRemoteAddressOfClientSocketsHandledByTheServer() {
        try {
            Field handledClientSocketsField = gomokuServer.getClass().getDeclaredField("handledClientSockets");
            handledClientSocketsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Set<Socket> handledClientSockets = (Set<Socket>) handledClientSocketsField.get(gomokuServer);

            return handledClientSockets.stream()
                    .unordered().parallel()
                    .map(Socket::getRemoteSocketAddress)
                    .collect(Collectors.toSet());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
            return ConcurrentHashMap.newKeySet();
        }
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