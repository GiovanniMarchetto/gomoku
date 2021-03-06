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
        super(returnValueIfValidOrElseThrow(value));
    }

    public PositiveInteger(@NotNull final PositiveInteger positiveInteger) {
        super(Objects.requireNonNull(positiveInteger));
    }

    public static boolean isPositiveIntegerFromString(@NotNull final String s) {
        try {
            return isValid(Integer.parseInt(s));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValid(int value) {
        return value > 0;
    }

    private static int returnValueIfValidOrElseThrow(int value) {
        if (isValid(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("The value must be positive");
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
