package it.units.sdm.gomoku.client_server;

import it.units.sdm.gomoku.client_server.fake_objects.EchoClient;
import it.units.sdm.gomoku.client_server.fake_objects.SimpleClient;
import it.units.sdm.gomoku.client_server.server.GomokuServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Set;

import static it.units.sdm.gomoku.client_server.GomokuProtocol.Status;
import static org.junit.jupiter.api.Assertions.*;

class GomokuProtocolTest {

    private GomokuServer gomokuServer;
    private Status currentStatus;
    private GomokuProtocol gomokuProtocol;

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
            Socket clientSocketToServer = (Socket)clientSocketToServerField.get(simpleClient);

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
}