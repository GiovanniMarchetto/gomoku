package it.units.sdm.gomoku.client_server;

import org.jetbrains.annotations.NotNull;

public interface Protocol {
    Object processInput(@NotNull final Object input);
}
