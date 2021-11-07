package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Player implements Observable {

    @NotNull
    private final String name;

    protected Player(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    public abstract void makeMove(@NotNull final Game currentGame) throws Board.BoardIsFullException;

    @Override
    public String toString() {
        return name;
    }
}
