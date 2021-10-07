package it.units.sdm.gomoku.custom_types;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

public class PositiveOddInteger extends NonNegativeInteger {

    /**
     * Initialize the number to 1.
     */
    public PositiveOddInteger() {
        super(1);
    }

    public PositiveOddInteger(@PositiveOddIntegerType int value) {
        super(value);
        if (value % 2 == 0) {
            throw new IllegalArgumentException("The value must be odd");
        }
    }

    /**
     * This annotation is used to indicate that the value must be a
     * positive (&gt;0) and odd integer, suitable for an instance of
     * {@link PositiveOddInteger}.
     */
    @Documented
    @Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE})
    public @interface PositiveOddIntegerType {
    }
}
