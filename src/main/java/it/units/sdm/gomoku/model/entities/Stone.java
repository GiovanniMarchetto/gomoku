package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Stone {

    @NotNull
    public final Color color;

    public Stone(@NotNull final Color stoneColor) {
        this.color = Objects.requireNonNull(stoneColor);
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
