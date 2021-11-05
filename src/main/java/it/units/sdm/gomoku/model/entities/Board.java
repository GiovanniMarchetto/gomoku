package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Board implements Observable, Cloneable, Serializable {

    public static final String boardMatrixPropertyName = "matrix";

    @NotNull
    private final PositiveInteger size;
    @NotNull
    private final List<Coordinates> coordinatesHistory;
    @NotNull
    private final Stone[][] matrix;


    public Board(@NotNull PositiveInteger size) {
        this.size = size;
        coordinatesHistory = new ArrayList<>(size.intValue());
        this.matrix = IntStream.range(0, size.intValue())
                .unordered().parallel()
                .mapToObj(a -> IntStream.range(0, size.intValue())
                        .mapToObj(x -> Stone.NONE)
                        .toArray(Stone[]::new))
                .toArray(Stone[][]::new);
    }

    public Board(@PositiveIntegerType int size) {
        this(new PositiveInteger(size));
    }

    public Board(@NotNull Board board) {
        this.size = new PositiveInteger(board.size);
        this.coordinatesHistory = new ArrayList<>(board.coordinatesHistory);
        this.matrix = board.getBoardMatrixCopy();
    }

    public boolean isCoordinatesBelongingToChainOfNStones(@NotNull final Coordinates coords, NonNegativeInteger N) {
        Stone stone = getStoneAtCoordinates(Objects.requireNonNull(coords));
        if (stone.isNone()) {
            throw new IllegalArgumentException("Given coordinates cannot refer to Stone." + stone);
        }
        return Stream.of(rowToList(coords), columnToList(coords), fwdDiagonalToList(coords), bckDiagonalToList(coords))
                .unordered().parallel()
                .anyMatch(stones -> Stone.isListContainingChainOfNStones(stones, N, stone));
    }

    @NotNull
    public Stone getStoneAtCoordinates(@NotNull final Coordinates coordinates) {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            return matrix[coordinates.getX()][coordinates.getY()];
        } else {
            throw new IndexOutOfBoundsException("Coordinate " + coordinates + " not present in the board.");
        }
    }

    @NotNull
    public List<Coordinates> getCoordinatesHistory() {
        return coordinatesHistory;
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
        return coordinatesHistory.size() == 0;
    }

    public synchronized boolean isAnyEmptyPositionOnTheBoard() {
        return coordinatesHistory.size() < Math.pow(size.intValue(), 2);
    }

    public synchronized void occupyPosition(@NotNull Stone stone, @NotNull Coordinates coordinates)
            throws NoMoreEmptyPositionAvailableException, PositionAlreadyOccupiedException {
        if (isAnyEmptyPositionOnTheBoard()) {
            if (isCoordinatesEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, Objects.requireNonNull(stone));
                coordinatesHistory.add(coordinates);
                firePropertyChange(boardMatrixPropertyName, this);
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
                && coordinatesHistory.size() == otherBoard.coordinatesHistory.size()
                && Arrays.deepEquals(matrix, otherBoard.matrix);
    }

    @Override
    public String toString() {
        int lengthOfSize = String.valueOf(size).length();
        String spacesToCompensateDigits = IntStream.range(0, lengthOfSize)
                .mapToObj(n -> " ").collect(Collectors.joining());
        String spacesIndexColumns = ((size.intValue() < 11) ? "   " : "  ");

        return spacesIndexColumns +
                spacesToCompensateDigits +
                IntStream.range(0, size.intValue())
                        .mapToObj(col ->
                                getSpacesForEveryCharOfSizeMoreThanInt(col)
                                        + col
                                        + "  "
                        ).collect(Collectors.joining()) +
                "\n" +
                IntStream.range(0, size.intValue()).mapToObj(row -> "" +
                        getSpacesForEveryCharOfSizeMoreThanInt(row)
                        + row
                        + "| "
                        + IntStream.range(0, size.intValue()).mapToObj(col -> " " +
                                switch (matrix[row][col]) {
                                    case BLACK -> "X";
                                    case WHITE -> "O";
                                    default -> " ";
                                }
                                + spacesToCompensateDigits)
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
