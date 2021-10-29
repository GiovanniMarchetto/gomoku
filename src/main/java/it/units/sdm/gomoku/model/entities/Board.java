package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Board implements Observable, Cloneable {

    public static final String boardMatrixPropertyName = "matrix";
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

    public Board(@NotNull Board board) {
        this.size = new PositiveInteger(board.size);
        this.matrix = board.getBoardMatrixCopy();
        this.numberOfFilledPositionOnTheBoard = new NonNegativeInteger(board.numberOfFilledPositionOnTheBoard);
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
    public Board.Stone getStoneAtCoordinates(@NotNull final Coordinates coordinates) {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            return matrix[coordinates.getX()][coordinates.getY()];
        } else {
            throw new IndexOutOfBoundsException("Coordinate " + coordinates + " not present in the board.");
        }
    }

    public Stone[][] getBoardMatrix() {
        return matrix;
    }

    public Stone[][] getBoardMatrixCopy() {
        return Arrays.stream(matrix)
                .map(Stone[]::clone)
                .toArray(Stone[][]::new);
    }

    @PositiveIntegerType
    public int getSize() {
        return size.intValue();
    }

    public boolean isCoordinatesInsideBoard(@NotNull Coordinates coordinates) {
        int x = Objects.requireNonNull(coordinates).getX();
        int y = coordinates.getY();
        return x < size.intValue() && y < size.intValue();
    }

    public boolean isEmpty() {
        return numberOfFilledPositionOnTheBoard.intValue() == 0;
    }

    public synchronized boolean isAnyEmptyPositionOnTheBoard() {
        return numberOfFilledPositionOnTheBoard.intValue() < Math.pow(size.intValue(), 2);
    }

    public synchronized void occupyPosition(@NotNull Board.Stone stone, @NotNull Coordinates coordinates)
            throws NoMoreEmptyPositionAvailableException, PositionAlreadyOccupiedException {
        if (isAnyEmptyPositionOnTheBoard()) {
            if (isCoordinatesEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, Objects.requireNonNull(stone));
                numberOfFilledPositionOnTheBoard.incrementAndGet();
                firePropertyChange(boardMatrixPropertyName, null, new ChangedCell(coordinates, stone, this));
            } else {
                throw new PositionAlreadyOccupiedException(coordinates);
            }
        } else {
            throw new NoMoreEmptyPositionAvailableException();
        }
    }

    private boolean isCoordinatesEmpty(@NotNull Coordinates coordinates) {
        return getStoneAtCoordinates(Objects.requireNonNull(coordinates)).isNone();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board otherBoard = (Board) o;
        return size.equals(otherBoard.size)
                && numberOfFilledPositionOnTheBoard.equals(otherBoard.numberOfFilledPositionOnTheBoard)
                && Arrays.deepEquals(matrix, otherBoard.matrix);
    }

    @Override
    public String toString() {
        int lengthOfSize = String.valueOf(size).length();
        String spaces = IntStream.range(0, lengthOfSize)
                .mapToObj(n -> " ").collect(Collectors.joining());

        return " " + spaces +
                IntStream.range(0, size.intValue())
                        .mapToObj(col ->
                                getSpacesForEveryCharOfSizeMoreThanInt(col)
                                        + col
                                        + " "
                        ).collect(Collectors.joining()) +
                "\n" +
                IntStream.range(0, size.intValue()).mapToObj(row -> "" +
                        getSpacesForEveryCharOfSizeMoreThanInt(row)
                        + row
                        + "| "
                        + IntStream.range(0, size.intValue()).mapToObj(col ->
                                switch (matrix[row][col]) {
                                    case BLACK -> "X";
                                    case WHITE -> "O";
                                    default -> " ";
                                }
                                        + spaces)
                        .collect(Collectors.joining())
                        + "\n"
                ).collect(Collectors.joining());
    }

    @NotNull
    private String getSpacesForEveryCharOfSizeMoreThanInt(int number) {
        int lengthOfSize = String.valueOf(size).length();
        return IntStream.range(0, lengthOfSize - String.valueOf(number).length())
                .mapToObj(n -> " ").collect(Collectors.joining());
    }

    @Override
    public Board clone() {
        return new Board(this);
    }

    @NotNull
    private List<Stone> diagonalToList(@NotNull final Coordinates coords, boolean isBackDiagonal) {
        int B = getSize();
        int sign = isBackDiagonal ? -1 : 1;
        int S = Objects.requireNonNull(coords).getX() + sign * coords.getY();

        return IntStream.range(0, B).sequential()
                .filter(i -> B + sign * i > sign * S && sign * i <= sign * S)
                // sign =  1 => B + i >  S &&  i <=  S
                // sign = -1 => B - i > -S && -i <= -S
                .boxed()
                .flatMap(i -> IntStream.range(0, B).unordered()
                        .filter(j -> i + sign * j == S)
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(this::getStoneAtCoordinates)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Stone> fwdDiagonalToList(@NotNull final Coordinates coords) {
        return diagonalToList(coords, false);
    }

    @NotNull
    private List<Stone> bckDiagonalToList(@NotNull final Coordinates coords) {
        return diagonalToList(coords, true);
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

        public boolean isNone() {
            return this == NONE;
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

    public static class ChangedCell {
        private final Coordinates coordinates;
        private final Stone newStone;
        private final Stone oldStone;
        private final Board board;

        public ChangedCell(Coordinates coordinates, Stone newStone, Stone oldStone, Board board) {
            this.coordinates = coordinates;
            this.newStone = newStone;
            this.oldStone = oldStone;
            this.board = board;
        }

        public ChangedCell(Coordinates coordinates, Stone newStone, Board board) {
            this(coordinates, newStone, Stone.NONE, board);
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public Stone getNewStone() {
            return newStone;
        }

        public Stone getOldStone() {
            return oldStone;
        }

        public Board getBoard() {
            return board;
        }
    }


}
