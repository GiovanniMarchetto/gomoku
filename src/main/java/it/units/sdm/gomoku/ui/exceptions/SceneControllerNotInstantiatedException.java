package it.units.sdm.gomoku.ui.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SceneControllerNotInstantiatedException extends IllegalStateException {
    public SceneControllerNotInstantiatedException(@NotNull final String errorMessage) {
        super(Objects.requireNonNull(errorMessage));
    }
}
