package it.units.sdm.gomoku.client_server;

import org.jetbrains.annotations.NotNull;

public interface Protocol {
    void processInput(@NotNull final Object input);
}
