package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    private final PositiveInteger size;
    @NotNull
    private final List<Coordinates> coordinatesHistory;
    @NotNull
    private final Cell[][] matrix;
    @NotNull
    private final ObservableProperty<Coordinates> lastMoveCoordinatesProperty;

    public Board(@NotNull PositiveInteger size) {
        this.size = size;
        this.coordinatesHistory = new ArrayList<>(size.intValue());
        this.matrix = IntStream.range(0, size.intValue())
                .mapToObj(i -> IntStream.range(0, size.intValue()).mapToObj(j -> new Cell()).toArray(Cell[]::new))
                .toArray(Cell[][]::new);
        this.lastMoveCoordinatesProperty = new ObservableProperty<>();
    }

    public Board(@PositiveIntegerType int size) {
        this(new PositiveInteger(size));
    }

    public Board(@NotNull Board board) {
        this.size = new PositiveInteger(board.size);
        this.coordinatesHistory = new ArrayList<>(board.coordinatesHistory);
        this.matrix = board.getBoardMatrixCopy();
        this.lastMoveCoordinatesProperty = board.lastMoveCoordinatesProperty.clone();
    }

    @PositiveIntegerType
    public int getSize() {
        return size.intValue();
    }

    @NotNull
    public List<Coordinates> getCoordinatesHistory() {
        return coordinatesHistory;
    }

    @NotNull
    public ObservableProperty<Coordinates> getLastMoveCoordinatesProperty() {
        return lastMoveCoordinatesProperty;
    }

    public boolean isEmpty() {
        return coordinatesHistory.size() == 0;
    }

    public synchronized boolean isThereAnyEmptyCell() {
        return coordinatesHistory.size() < Math.pow(size.intValue(), 2);
    }

    public boolean isCoordinatesInsideBoard(@NotNull Coordinates coordinates) {
        int x = Objects.requireNonNull(coordinates).getX();
        int y = coordinates.getY();
        return x < size.intValue() && y < size.intValue();
    }

    @NotNull
    public Cell getCellAtCoordinates(int x, int y) {
        return matrix[x][y];
    }

    @NotNull
    public Cell getCellAtCoordinates(@NotNull Coordinates coordinates) {
        return getCellAtCoordinates(coordinates.getX(), coordinates.getY());
    }

    public synchronized void occupyPosition(@NotNull Stone.Color stoneColor, @NotNull Coordinates coordinates)
            throws BoardIsFullException, CellAlreadyOccupiedException {
        if (isThereAnyEmptyCell()) {
            if (isCellEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, new Stone(Objects.requireNonNull(stoneColor)));
                coordinatesHistory
                        .add(lastMoveCoordinatesProperty.setPropertyValueAndFireIfPropertyChange(coordinates)
                                .getPropertyValue());
            } else {
                throw new CellAlreadyOccupiedException(coordinates);
            }
        } else {
            throw new BoardIsFullException();
        }
    }

    private boolean isCellEmpty(@NotNull Coordinates coordinates) {
        return getCellAtCoordinates(Objects.requireNonNull(coordinates)).isEmpty();
    }

    private void setStoneAtCoordinates(@NotNull final Coordinates coordinates, @Nullable Stone stone) {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            getCellAtCoordinates(coordinates).setStone(stone);
        } else {
            throw new IndexOutOfBoundsException("Coordinate " + coordinates + " not present in the board.");
        }
    }


    public boolean isCoordinatesBelongingToChainOfNStones(@NotNull final Coordinates coords, NonNegativeInteger N) {
        Cell cell = getCellAtCoordinates(Objects.requireNonNull(coords));
        if (cell.isEmpty()) {
            return false;
        }
        return Stream.of(rowToList(coords), columnToList(coords), fwdDiagonalToList(coords), bckDiagonalToList(coords))
                .unordered().parallel()
                .anyMatch(cellList -> isListContainingChainOfNCells(cellList, N, Objects.requireNonNull(cell)));
    }

    private static boolean isListContainingChainOfNCells(@NotNull final List<@NotNull Cell> cellList,
                                                         NonNegativeInteger N, @NotNull final Cell cell) {
        int numberOfStonesInChain = N.intValue();

        if (cellList.size() < numberOfStonesInChain)
            return false;

        return IntStream.range(0, cellList.size() - numberOfStonesInChain + 1)
                .unordered()
                .map(x -> cellList.subList(x, x + numberOfStonesInChain)
                        .stream()
                        .mapToInt(y -> y.equals(cell) ? 1 : 0/*type conversion*/)
                        .sum())
                .anyMatch(aSum -> aSum >= numberOfStonesInChain);
    }

    @NotNull
    private List<Cell> rowToList(@NotNull final Coordinates coords) {
        return new ArrayList<>(Arrays.asList(matrix[Objects.requireNonNull(coords).getX()]));
    }

    @NotNull
    private List<Cell> columnToList(@NotNull final Coordinates coords) {
        Objects.requireNonNull(coords);
        return IntStream
                .range(0, getSize())
                .sequential()
                .mapToObj(x -> getCellAtCoordinates(x, coords.getY()))
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Cell> fwdDiagonalToList(@NotNull final Coordinates coords) {
        return diagonalToList(coords, false);
    }

    @NotNull
    private List<Cell> bckDiagonalToList(@NotNull final Coordinates coords) {
        return diagonalToList(coords, true);
    }

    @NotNull
    private List<Cell> diagonalToList(@NotNull final Coordinates coords, boolean isBackDiagonal) {
        int boardSize = getSize();
        int sign = isBackDiagonal ? -1 : 1;
        int S = Objects.requireNonNull(coords).getX() + sign * coords.getY();

        return IntStream.range(0, boardSize).sequential()
                .filter(i -> boardSize + sign * i > sign * S && sign * i <= sign * S)
                // sign =  1 => boardSize + i >  S &&  i <=  S
                // sign = -1 => boardSize - i > -S && -i <= -S
                .boxed()
                .flatMap(i -> IntStream.range(0, boardSize).unordered()
                        .filter(j -> i + sign * j == S)
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(this::getCellAtCoordinates)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        int lengthOfSize = String.valueOf(size).length();

        return String.format("  %s%" + lengthOfSize + "s", size.intValue() < 11 ? " " : "", "") +
                IntStream.range(0, size.intValue())
                        .mapToObj(col -> String.format("%" + lengthOfSize + "d  ", col))
                        .collect(Collectors.joining()) +
                System.lineSeparator() +
                IntStream.range(0, size.intValue()).mapToObj(row ->
                                String.format("%" + lengthOfSize + "d| ", row)
                                        + IntStream.range(0, size.intValue())
                                        .mapToObj(col ->
                                                String.format(" %s%" + lengthOfSize + "s",
                                                        getCellAtCoordinates(row, col), ""))
                                        .collect(Collectors.joining())
                                        + System.lineSeparator())
                        .collect(Collectors.joining());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Board clone() {
        return new Board(this);
    }

    private Cell[][] getBoardMatrixCopy() {
        return Arrays.stream(matrix)
                .map(Cell[]::clone)
                .toArray(Cell[][]::new);
    }

    @Override
    public boolean equals(Object o) {   // TODO to be tested
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board otherBoard = (Board) o;
        return size.equals(otherBoard.size)
                && lastMoveCoordinatesProperty.valueEquals(otherBoard.lastMoveCoordinatesProperty)
                && coordinatesHistory.equals(otherBoard.coordinatesHistory) // TODO : board equality should consider coordinate history?
                && Arrays.deepEquals(matrix, otherBoard.matrix);
    }

    @Override
    public int hashCode() { // TODO : check if creates problems with hashmaps (hashCode method should be present according to equals() contract)
        // TODO to be tested
        int result = size.hashCode();
        result = 31 * result + coordinatesHistory.hashCode();
        result = 31 * result + Arrays.deepHashCode(matrix);
        result = 31 * result + lastMoveCoordinatesProperty.hashCode();
        return result;
    }

    public static class BoardIsFullException extends Exception {

        public BoardIsFullException() {
            super("The board is entirely filled. No more space available.");
        }
    }

    public static class CellAlreadyOccupiedException extends Exception {

        public CellAlreadyOccupiedException(@NotNull final Coordinates coordinates) {
            super(Objects.requireNonNull(coordinates) + " already occupied.");
        }
    }
}