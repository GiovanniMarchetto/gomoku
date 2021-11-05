package it.units.sdm.gomoku.model.custom_types;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.NonNegativeIntegerType;

public class Coordinates extends Pair<NonNegativeInteger, NonNegativeInteger> {

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

}