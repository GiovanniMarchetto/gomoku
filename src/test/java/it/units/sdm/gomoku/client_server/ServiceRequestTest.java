package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.fake_objects.EchoClient;
import it.units.sdm.gomoku.client_server.fake_objects.EchoProtocol;
import it.units.sdm.gomoku.client_server.fake_objects.ServerThatAcceptsOnlyOneRequestAndSavesItInField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

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


    @BeforeEach
    void setUp() {
        try {
             serverSingleRequest =
                    new ServerThatAcceptsOnlyOneRequestAndSavesItInField();
            echoClient = new EchoClient(new PrintStream(clientOutputStream, true));
            Thread echoServerThread = new Thread(serverSingleRequest);
            echoClientThread = new Thread(echoClient);
            echoServerThread.start();
            echoClientThread.start();
            echoServerThread.join();
            serviceRequest = new ServiceRequest(
                    serverSingleRequest.getAcceptedClientSocketOrNullIfNoClientsConnected(), echoProtocol);
        } catch (IOException | InterruptedException e) {
            fail(e);
        }
    }

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

    @Test
    void testServiceRequest() {
        Thread serviceRequestThread = new Thread(serviceRequest);
        serviceRequestThread.start();
        try {
            serviceRequestThread.join();
            echoClientThread.join();
            clientInterrupted = true;
            String expectedReceivedFromServer = EchoClient.SENT_TO_SERVER;
            assertEquals(expectedReceivedFromServer, clientOutputStream.toString());
        } catch (InterruptedException e) {
            fail(e);
        }
    }
}

