package it.units.sdm.gomoku.model.custom_types;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.util.Objects;

import static java.lang.annotation.ElementType.*;

public class PositiveInteger extends NonNegativeInteger {

    public PositiveInteger() {
        super(1);
    }

    public PositiveInteger(@PositiveIntegerType int value) {
        super(value);
        if (value <= 0) {
            throw new IllegalArgumentException("The value must be positive");
        }
    }

    public PositiveInteger(@NotNull final PositiveInteger positiveInteger) {
        super(Objects.requireNonNull(positiveInteger));
    }

    public static boolean isPositiveInteger(@NotNull final String s) {
        try {
            new PositiveInteger(Integer.parseInt(s));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This annotation is used to indicate that the value must be a
     * positive (&gt;0) integer, suitable for an instance of
     * {@link PositiveInteger}.
     */
    @Documented
    @Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE})
    public @interface PositiveIntegerType {
    }
}
