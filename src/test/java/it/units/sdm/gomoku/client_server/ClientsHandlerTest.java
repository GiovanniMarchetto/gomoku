package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.fake_objects.EchoClient;
import it.units.sdm.gomoku.client_server.interfaces.Protocol;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ClientsHandlerTest {

    private ExecutorService serviceRequestsOfClientsExecutorService;
    private final static int N_CONCURRENT_SERVED_REQUESTS = 10;
    private Set<Socket> handledClientSockets;

    @BeforeEach
    void setUp() {
        serviceRequestsOfClientsExecutorService =
                Executors.newFixedThreadPool(N_CONCURRENT_SERVED_REQUESTS);
        handledClientSockets = ConcurrentHashMap.newKeySet();
    }

    @AfterEach
    void tearDown() {
        serviceRequestsOfClientsExecutorService.shutdown();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 100, 1000})
    void run(int numberOfRequests) {

        try {
            ServerSocket serverSocket =
                    new ServerSocket(ClientServerUtility.SERVER_PORT_NUMBER);
            Thread serverThread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        handledClientSockets.add(serverSocket.accept());
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            fail(e);
                        }
                    }
                }
            });
            serverThread.start();

            CountHowManyServedRequestsProtocol protocol =
                    new CountHowManyServedRequestsProtocol();
            ClientsHandler clientsHandler = new ClientsHandler(
                    handledClientSockets,
                    protocol,
                    serviceRequestsOfClientsExecutorService);
            Thread clientHandlerThread = new Thread(clientsHandler);
            clientHandlerThread.start();

            IntStream.range(0, numberOfRequests)
                    .unordered().parallel()
                    .mapToObj(i -> {
                        try {
                            return new EchoClient(
                                    "",
                                    new PrintStream(new ByteArrayOutputStream()));
                        } catch (IOException e) {
                            fail(e);
                            return null;
                        }
                    })
                    .forEach(aClient -> {
                        Thread clientThread = new Thread(aClient);
                        clientThread.start();
                        try {
                            clientThread.join();
                            aClient.close();
                        } catch (InterruptedException | IOException ignored) {
                        }
                    });

            serverSocket.close();
            serverThread.join();

            assertEquals(numberOfRequests, protocol.getNumberOfServedRequests());

        } catch (IOException | InterruptedException e) {
            fail(e);
        }

    }
}

class CountHowManyServedRequestsProtocol implements Protocol {

    private final AtomicInteger numberOfServedRequests =
            new AtomicInteger(0);

    public synchronized int getNumberOfServedRequests() {
        return numberOfServedRequests.get();
    }

    @Override
    public Object processInput(@NotNull Object input) {
        return numberOfServedRequests.incrementAndGet();
    }
}