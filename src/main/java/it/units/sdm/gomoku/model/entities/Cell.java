package it.units.sdm.gomoku.model.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Cell implements Cloneable {
    // TODO: TEST THIS
    @Nullable
    private volatile Stone stone;

    public Cell() {
    }

    public Cell(@NotNull Cell cell) {
        this.stone = Objects.requireNonNull(cell).stone;
    }

    @Nullable
    public Stone getStone() {
        return stone;
    }

    public synchronized void setStone(@Nullable Stone stone) {
        this.stone = stone;
    }

    public boolean isEmpty() {
        return this.stone == null;
    }

    @Override
    public Cell clone() {
        return new Cell(this);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return " ";
        } else {
            return switch (Objects.requireNonNull(stone).color()) {
                case BLACK -> String.valueOf('\u2b24'); // Unicode Character “⬤” (U+2B24) Black Large Circle
                case WHITE -> "O";
            };
        }
    }
}