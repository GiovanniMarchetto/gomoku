package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public enum Stone {
    NONE,
    BLACK,
    WHITE;

    public static boolean isListContainingChainOfNStones(
            @NotNull final List<@NotNull Stone> list,
            NonNegativeInteger N, @NotNull final Stone stone) {

        int n = N.intValue();

        if (list.size() < n)
            return false;

        return IntStream.range(0, list.size() - n + 1)
                .unordered()
                .map(x -> list.subList(x, x + n)
                        .stream()
                        .mapToInt(y -> y == stone ? 1 : 0/*type conversion*/)
                        .sum())
                .anyMatch(aSum -> aSum >= n);
    }

    public boolean isNone() {
        return this == NONE;
    }
}
