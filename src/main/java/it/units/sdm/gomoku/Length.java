package it.units.sdm.gomoku;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Specify the length of an array to which the annotation is referred.
 */
@Documented
@Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE})
public @interface Length {
    /**
     * @return the length of the array
     */
    long length();
}