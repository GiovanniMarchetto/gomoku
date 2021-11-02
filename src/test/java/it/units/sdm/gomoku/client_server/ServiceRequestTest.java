package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.fake_objects.EchoClient;
import it.units.sdm.gomoku.client_server.fake_objects.EchoProtocol;
import it.units.sdm.gomoku.client_server.fake_objects.ServerThatAcceptsOnlyOneRequestAndSavesItInField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ServiceRequestTest {  // TODO : refactor/simplify ?

    private ServiceRequest serviceRequest;
    private final Protocol echoProtocol = new EchoProtocol();
    private ServerThatAcceptsOnlyOneRequestAndSavesItInField serverSingleRequest;
    private Client echoClient;
    private Thread echoClientThread;
    private final ByteArrayOutputStream clientOutputStream = new ByteArrayOutputStream();
    private boolean clientInterrupted = false;

    private static final String STRING_SENT_FROM_ECHO_CLIENT = "Hello World";

    @AfterEach
    void tearDown() {
        try {
            if (!clientInterrupted) {
                echoClientThread.join();
            }
        } catch (InterruptedException e) {
            fail(e);
        }
        try {
            serverSingleRequest.close();
            echoClient.close();
        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello world", "0", "", "^"})
    void testServiceRequest(String stringToEcho) {  // TODO : long test (refactor needed)
        try {
            serverSingleRequest =
                    new ServerThatAcceptsOnlyOneRequestAndSavesItInField();
            Thread echoServerThread = new Thread(serverSingleRequest);
            echoServerThread.start();

            echoClient = new EchoClient(stringToEcho,
                    new PrintStream(clientOutputStream, true));

            echoClientThread = new Thread(echoClient);
            echoClientThread.start();
            echoServerThread.join();
            serviceRequest = new ServiceRequest(
                    serverSingleRequest.getAcceptedClientSocketOrNullIfNoClientsConnected(), echoProtocol);

            Thread serviceRequestThread = new Thread(serviceRequest);
            serviceRequestThread.start();
            try {
                serviceRequestThread.join();
                echoClientThread.join();
                clientInterrupted = true;
                assertEquals(stringToEcho, clientOutputStream.toString());
            } catch (InterruptedException e) {
                fail(e);
            }
        } catch (IOException | InterruptedException e) {
            fail(e);
        }
    }
}

