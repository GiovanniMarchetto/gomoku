package it.units.sdm.gomoku.model.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Predicates {

    public static final Predicate<String> isNonEmptyString = str -> !str.trim().isEmpty();

    public static final BiPredicate<@NotNull Object[][], @NotNull Integer> isSquareMatrixOfGivenSize = (matrix, matrixSize) ->
            Objects.requireNonNull(matrix).length == Objects.requireNonNull(matrixSize) &&
                    Arrays.stream(matrix)
                            .filter(aRow_asArray -> Objects.nonNull(aRow_asArray) && aRow_asArray.length == matrixSize)
                            .count() == matrixSize;

}
