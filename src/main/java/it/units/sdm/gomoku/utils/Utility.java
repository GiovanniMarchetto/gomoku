package it.units.sdm.gomoku.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class Utility {

    public static boolean isEvenNumber(int n) {
        return n % 2 == 0;
    }

    public static Logger getLoggerOfClass(@NotNull final Class<?> targetClass) {
        return Logger.getLogger(Objects.requireNonNull(targetClass).getCanonicalName());
    }

}
