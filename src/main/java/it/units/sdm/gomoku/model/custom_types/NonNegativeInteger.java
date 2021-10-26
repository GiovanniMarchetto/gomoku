package it.units.sdm.gomoku.model.custom_types;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

public class NonNegativeInteger extends Number implements Comparable<Number> {

    private int value;

    public NonNegativeInteger(@NonNegativeIntegerType int value) {
        if (value >= 0) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("The value must be non negative.");
        }
    }

    public NonNegativeInteger() {
        this(0);
    }

    @NotNull
    public synchronized NonNegativeInteger incrementAndGet() {
        value++;
        return this;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonNegativeInteger that = (NonNegativeInteger) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public int compareTo(@NotNull Number o) {
        return Double.compare(value, o.doubleValue());
    }

    /**
     * This annotation is used to indicate that the value must be a
     * non-negative (&ge;0) integer, suitable for an instance of
     * {@link NonNegativeInteger}.
     */
    @Documented
    @Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE})
    public @interface NonNegativeIntegerType {
    }

}