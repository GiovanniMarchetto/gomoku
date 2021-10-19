package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.Length;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.NonNegativeIntegerType;

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

    public Coordinates(@NotNull @Length(length = 2) final NonNegativeInteger... coordinates) {
        this(validateCoordsFromVarargs(coordinates)[0], coordinates[1]);
    }

    public Coordinates(@NonNegativeIntegerType int x, @NonNegativeIntegerType int y) {
        this(new NonNegativeInteger(x), new NonNegativeInteger(y));
    }

    @NotNull
    @Length(length = 2)
    private static NonNegativeInteger[] validateCoordsFromVarargs(@NotNull final NonNegativeInteger[] coordinates) {
        if (Objects.requireNonNull(coordinates).length != 2) {
            throw new IllegalArgumentException("2 coordinates expected but " + coordinates.length + " found.");
        }
        return coordinates;
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
