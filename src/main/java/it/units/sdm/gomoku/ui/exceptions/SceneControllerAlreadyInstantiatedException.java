package it.units.sdm.gomoku.ui.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SceneControllerAlreadyInstantiatedException extends IllegalStateException {
    public SceneControllerAlreadyInstantiatedException(@NotNull final String errorMessage) {
        super(Objects.requireNonNull(errorMessage));
    }
}
