package it.units.sdm.gomoku.utils;

import java.util.function.Predicate;

public class Predicates {

    public static final Predicate<String> isNonEmptyString = str -> !str.trim().isEmpty();

}
