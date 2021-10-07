package it.units.sdm.gomoku.entities;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Player {

    @NotNull
    private final String name;

    public Player(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
