package it.units.sdm.gomoku.client_server.interfaces;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Protocol {
    Object processInput(@NotNull final Object input) throws IOException;
}
