package it.units.sdm.gomoku.model.custom_types;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

public class PositiveInteger extends NonNegativeInteger {
    /**
     * Initialize the number to 1.
     */
    public PositiveInteger() {
        super(1);
    }

    public PositiveInteger(@PositiveIntegerType int value) {
        super(value);
        if (value <= 0) {
            throw new IllegalArgumentException("The value must be positive");
        }
    }

    public PositiveInteger(PositiveInteger positiveInteger){
        this(positiveInteger.intValue());
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
