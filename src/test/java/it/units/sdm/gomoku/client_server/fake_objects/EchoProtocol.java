package it.units.sdm.gomoku.client_server.fake_objects;

import it.units.sdm.gomoku.client_server.Protocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EchoProtocol implements Protocol {
    @Override
    public Object processInput(@NotNull Object input) {
        return getIfStringOrNull(input);
    }

    @Nullable
    private String getIfStringOrNull(@NotNull Object input) {
        if (input instanceof String inputAsString) {
            return inputAsString;
        } else {
            return null;
        }
    }
}
