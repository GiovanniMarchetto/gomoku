package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.custom_types.PositiveInteger.PositiveIntegerType;

public class Board {

    @NotNull
    private final PositiveInteger size;
    @NotNull
    private final Stone[][] matrix;
    @NotNull
    private final NonNegativeInteger numberOfFilledPositionOnTheBoard;

    public Board(@NotNull PositiveInteger size) {
        this.size = size;
        this.matrix = IntStream.range(0, size.intValue())
                .unordered().parallel()
                .mapToObj(a -> IntStream.range(0, size.intValue())
                        .mapToObj(x -> Stone.NONE)
                        .toArray(Stone[]::new))
                .toArray(Stone[][]::new);
        this.numberOfFilledPositionOnTheBoard = new NonNegativeInteger(0);
    }

    public Board(@PositiveIntegerType int size) {
        this(new PositiveInteger(size));
    }

    @NotNull
    public Board.Stone getStoneAtCoordinates(@NotNull final Coordinates coordinates) {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            return matrix[coordinates.getX()][coordinates.getY()];
        } else {
            throw new IndexOutOfBoundsException("Coordinate " + coordinates + " not present in the board.");
        }
    }

    private void setStoneAtCoordinates(@NotNull final Coordinates coordinates, @NotNull Stone stone) {
        if (!stone.isNone()) {
            if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
                matrix[coordinates.getX()][coordinates.getY()] = stone;
            } else {
                throw new IndexOutOfBoundsException("Coordinate " + coordinates + " not present in the board.");
            }
        } else {
            throw new IllegalArgumentException("Stone color cannot be " + Stone.NONE);
        }
    }

    public Stone[][] getBoard() {
        return Arrays.stream(matrix)
                .map(Stone[]::clone)
                .toArray(Stone[][]::new);
    }

    @PositiveIntegerType
    public int getSize() {
        return size.intValue();
    }

    private boolean isCoordinatesEmpty(@NotNull Coordinates coordinates) {
        return getStoneAtCoordinates(Objects.requireNonNull(coordinates)).isNone();
    }

    public boolean isCoordinatesInsideBoard(@NotNull Coordinates coordinates) {
        int x = Objects.requireNonNull(coordinates).getX();
        int y = coordinates.getY();
        return x < size.intValue() && y < size.intValue();
    }

    public boolean isAnyEmptyPositionOnTheBoard() {
        return numberOfFilledPositionOnTheBoard.intValue() < size.intValue() * size.intValue();
    }

    public void occupyPosition(@NotNull Board.Stone player, @NotNull Coordinates coordinates)
            throws NoMoreEmptyPositionAvailableException, PositionAlreadyOccupiedException {
        if (isAnyEmptyPositionOnTheBoard()) {
            if (isCoordinatesEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, Objects.requireNonNull(player));
                numberOfFilledPositionOnTheBoard.incrementAndGet();
            } else {
                throw new PositionAlreadyOccupiedException(coordinates);
            }
        } else {
            throw new NoMoreEmptyPositionAvailableException();
        }
    }

    @Override
    public String toString() {
        //TODO: re-implement toString()
        StringBuilder s = new StringBuilder();
        StringBuilder indCol = new StringBuilder();
        indCol.append("   ");
        for (int i = 0; i < size.intValue(); i++) {
            if (i < 10) {
                s.append(" ");
                indCol.append(" ");
            }
            s.append(i).append("| ");
            indCol.append(i).append(" ");//square board
            for (int j = 0; j < size.intValue(); j++) {
                if (matrix[i][j] == null) s.append(" ");
                else
                    switch (matrix[i][j]) {
                        case BLACK -> s.append("X");
                        case WHITE -> s.append("O");
                        default -> s.append(" ");
                    }
                s.append("  ");
            }
            s.append("\n");
        }
        indCol.append("\n");
        s.insert(0, indCol);
        return s.toString();
    }

    public boolean checkNConsecutiveStones(@NotNull final Coordinates coords, NonNegativeInteger N) {
        Stone stone = getStoneAtCoordinates(Objects.requireNonNull(coords));
        if (stone.isNone()) {
            throw new IllegalArgumentException("Given coordinates cannot refer to Stone." + stone);
        }
        return Stream.of(rowToList(coords), columnToList(coords), fwdDiagonalToList(coords), bckDiagonalToList(coords))
                .unordered().parallel()
                .anyMatch(stones -> Stone.listContainsNConsecutiveStones(stones, N, stone));
    }

    @NotNull
    private List<Stone> fwdDiagonalToList(@NotNull final Coordinates coords) {
        // TODO : very similar to bckDiagonalToList (refactoring?)
        int S = getSize();
        int diagN = Objects.requireNonNull(coords).getX() + coords.getY();
        int x = diagN < S ? diagN : S - 1;
        int y = diagN < S ? 0 : diagN - (S - 1);
        ArrayList<Stone> arr = new ArrayList<>();
        while (y < S && x >= 0) {
            arr.add(matrix[x][y]);
            x--;
            y++;
        }
        return arr;
    }

    @NotNull
    private ArrayList<Stone> bckDiagonalToList(@NotNull final Coordinates coords) {
        int S = getSize();
        int diagN = Objects.requireNonNull(coords).getX() - coords.getY();
        int x = Math.max(diagN, 0);
        int y = -Math.min(diagN, 0);
        ArrayList<Stone> arr = new ArrayList<>();
        while (x < S && y < S) {
            arr.add(matrix[x][y]);
            x++;
            y++;
        }
        return arr;
    }

    @NotNull
    private List<Stone> columnToList(@NotNull final Coordinates coords) {
        Objects.requireNonNull(coords);
        return IntStream
                .range(0, getSize())
                .sequential()
                .mapToObj(x -> matrix[x][coords.getY()])
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Stone> rowToList(@NotNull final Coordinates coords) {
        return new ArrayList<>(Arrays.asList(matrix[Objects.requireNonNull(coords).getX()]));
    }

    public enum Stone {
        NONE,
        BLACK,
        WHITE;

        public boolean isNone() {
            return this == NONE;
        }

        private static boolean listContainsNConsecutiveStones(
                @NotNull final List<@NotNull Stone> list,
                NonNegativeInteger N, @NotNull final Board.Stone stone) {

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
    }

    public static class NoMoreEmptyPositionAvailableException extends Exception {
        public NoMoreEmptyPositionAvailableException() {
            super("The board is entirely filled. No more space available.");
        }
    }

    public static class PositionAlreadyOccupiedException extends Exception {
        public PositionAlreadyOccupiedException(@NotNull final Coordinates coordinates) {
            super(Objects.requireNonNull(coordinates) + " already occupied.");
        }
    }


}
