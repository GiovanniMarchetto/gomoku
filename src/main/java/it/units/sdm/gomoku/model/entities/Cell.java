package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Cell implements Cloneable {

    @Nullable
    private Stone stone;

    public Cell() {
    }

    public Cell(@NotNull Cell cell) {
        this.stone = Objects.requireNonNull(cell).stone;
    }

    @Nullable
    public synchronized Stone getStone() {
        return stone;
    }

    private synchronized void setStone(@Nullable Stone stone) {
        this.stone = stone;
    }

    public synchronized void setStoneFromColor(@NotNull Color color) {
        setStone(new Stone(Objects.requireNonNull(color)));
    }

    public synchronized boolean isEmpty() {
        return this.stone == null;
    }

    public synchronized boolean isBelongingToChainOfNCellsInList(@NotNull final NonNegativeInteger N,
                                                                 @NotNull final List<@NotNull Cell> cellList) {
        int numberOfStonesInChain = Objects.requireNonNull(N).intValue();
        if (cellList.size() < numberOfStonesInChain) {
            return false;
        }
        return IntStream.range(0, cellList.size() - numberOfStonesInChain + 1)
                .unordered()
                .map(x -> (int) cellList.subList(x, x + numberOfStonesInChain)
                        .stream()
                        .filter(this::equals)
                        .count())
                .anyMatch(count -> count >= numberOfStonesInChain);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public synchronized Cell clone() {
        return new Cell(this);
    }

    @Override
    public synchronized String toString() {
        if (isEmpty()) {
            return " ";
        } else {
            return switch (Objects.requireNonNull(stone).getColor()) {
                case BLACK -> "X";
                case WHITE -> "O";
            };
        }
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(stone, ((Cell) o).stone);
    }

    @Override
    public synchronized int hashCode() {
        return stone != null ? stone.hashCode() : 0;
    }
}