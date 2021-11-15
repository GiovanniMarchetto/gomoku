package it.units.sdm.gomoku.model.entities;

import org.jetbrains.annotations.NotNull;

public class Stone {    // TODO: test

    public enum Color {BLACK, WHITE}

    @NotNull
    public final Color color;

    public Stone(@NotNull final Color stoneColor) {
        this.color = stoneColor;
    }

    @NotNull
    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stone stone = (Stone) o;
        return color == stone.color;
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }
}
