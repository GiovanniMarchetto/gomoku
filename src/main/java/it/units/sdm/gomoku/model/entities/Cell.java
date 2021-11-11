package it.units.sdm.gomoku.model.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Cell implements Cloneable {

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

    @SuppressWarnings("MethodDoesntCallSuperMethod")
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
                case BLACK -> "X"; // Unicode Character “⬤” (U+2B24) Black Large Circle
                case WHITE -> "O";
            };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(stone, ((Cell) o).stone);
    }

    @Override
    public int hashCode() {
        //noinspection ConstantConditions // already check
        return stone != null ? stone.hashCode() : 0;
    }
}