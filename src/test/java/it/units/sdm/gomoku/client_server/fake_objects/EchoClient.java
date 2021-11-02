package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.Client;
import it.units.sdm.gomoku.client_server.ClientServerUtility;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient implements Client {    // TODO : to be tested
    public static final String SENT_TO_SERVER = "Hello World";
    private final Socket socketToServer;   // TODO : Client may be an Abstract class
    private final PrintStream outputPrintStream;

    public EchoClient(@NotNull final PrintStream out) throws IOException {
        this.socketToServer = new Socket(
                ClientServerUtility.LOOPBACK_HOSTNAME,
                ClientServerUtility.SERVER_PORT_NUMBER);
        this.outputPrintStream = Objects.requireNonNull(out);
    }

    @Override
    public void close() throws IOException {
        socketToServer.close();
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socketToServer.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socketToServer.getInputStream()))
        ) {
            out.println(SENT_TO_SERVER);
            String fromServer = in.readLine();
            outputPrintStream.print(fromServer);
        } catch (IOException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "I/O Exception", e);
        }
    }
}
