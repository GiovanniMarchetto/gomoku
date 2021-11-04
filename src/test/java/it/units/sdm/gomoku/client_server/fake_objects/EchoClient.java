package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.ClientServerUtility;
import it.units.sdm.gomoku.client_server.interfaces.Client;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient implements Client {    // TODO : to be tested

    private final Socket socketToServer;   // TODO : Client may be an Abstract class
    private final PrintStream outputPrintStream;
    private final String toSendToServer;

    public EchoClient(@NotNull final String stringToSendToServer,
                      @NotNull final PrintStream out) throws IOException {
        this.toSendToServer = Objects.requireNonNull(stringToSendToServer);
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
                ObjectOutputStream out =
                        new ObjectOutputStream(socketToServer.getOutputStream());
                ObjectInputStream in =
                        new ObjectInputStream(socketToServer.getInputStream())
        ) {
            out.writeObject(toSendToServer);
            out.flush();
            String fromServer = String.valueOf(in.readObject());
            outputPrintStream.print(fromServer);
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "I/O Exception", e);
        }
    }
}
