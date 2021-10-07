package it.units.sdm.gomoku.custom_types;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static it.units.sdm.gomoku.custom_types.NonNegativeInteger.NonNegativeIntegerType;

public class Coordinates extends Pair<NonNegativeInteger, NonNegativeInteger> {

    /**
     * Creates a new pair of coordinates
     *
     * @param x The x value
     * @param y The y value
     */
    public Coordinates(@NotNull NonNegativeInteger x, @NotNull NonNegativeInteger y) {
        super(Objects.requireNonNull(x), Objects.requireNonNull(y));
    }

    public Coordinates(@NonNegativeIntegerType int x, @NonNegativeIntegerType int y) {
        this(new NonNegativeInteger(x), new NonNegativeInteger(y));
    }

    @NonNegativeIntegerType
    public int getX() {
        return getKey().intValue();
    }

    @NonNegativeIntegerType
    public int getY() {
        return getValue().intValue();
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + ")";
    }

    public static class InvalidCoordinateException extends Exception {
        public InvalidCoordinateException() {
            super();
        }

        public InvalidCoordinateException(@NotNull String msg) {
            super(Objects.requireNonNull(msg));
        }
    }
}
