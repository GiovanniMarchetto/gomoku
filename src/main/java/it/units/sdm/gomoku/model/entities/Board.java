package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
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

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.NonNegativeIntegerType;
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

    public boolean isThereAnyEmptyCell() {
        return coordinatesHistory.size() < Math.pow(size.intValue(), 2);
    }

    public boolean isCoordinatesInsideBoard(@NotNull Coordinates coordinates) {
        @NonNegativeIntegerType int x = Objects.requireNonNull(coordinates).getX();
        @NonNegativeIntegerType int y = coordinates.getY();
        return x < size.intValue() && y < size.intValue();
    }

    @NotNull
    public Cell getCellAtCoordinates(@NonNegativeIntegerType int x,
                                     @NonNegativeIntegerType int y) throws CellOutOfBoardException {
        return getCellAtCoordinates(new Coordinates(x, y));
    }

    @NotNull
    public Cell getCellAtCoordinates(@NotNull final Coordinates coordinates) throws CellOutOfBoardException {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            return matrix[coordinates.getX()][coordinates.getY()];
        } else {
            throw new CellOutOfBoardException(coordinates);
        }
    }

    @Nullable
    public Cell getCellAtCoordinatesOrNullIfInvalid(@NotNull final Coordinates coordinates) {
        try {
            return getCellAtCoordinates(Objects.requireNonNull(coordinates));
        } catch (CellOutOfBoardException e) {
            return null;
        }
    }

    public synchronized void occupyPosition(@NotNull Stone.Color stoneColor, @NotNull Coordinates coordinates)
            throws BoardIsFullException, CellAlreadyOccupiedException, CellOutOfBoardException {
        if (isThereAnyEmptyCell()) {
            if (isCellEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, new Stone(Objects.requireNonNull(stoneColor)));
                coordinatesHistory.add(
                        lastMoveCoordinatesProperty
                                .setPropertyValueAndFireIfPropertyChange(coordinates)
                                .getPropertyValue());
            } else {
                throw new CellAlreadyOccupiedException(coordinates);
            }
        } else {
            throw new BoardIsFullException();
        }
    }

    private boolean isCellEmpty(@NotNull final Coordinates coordinates) throws CellOutOfBoardException {
        return getCellAtCoordinates(Objects.requireNonNull(coordinates)).isEmpty();
    }

    private synchronized void setStoneAtCoordinates(@NotNull final Coordinates coordinates, @Nullable Stone stone) throws CellOutOfBoardException {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            getCellAtCoordinates(coordinates).setStone(stone);
        } else {
            throw new CellOutOfBoardException(coordinates);
        }
    }


    public boolean isCoordinatesBelongingToChainOfNStones(@NotNull final Coordinates coords,
                                                          @NotNull final NonNegativeInteger N) {
        Cell cell;
        try {
            cell = getCellAtCoordinates(Objects.requireNonNull(coords));
        } catch (CellOutOfBoardException e) {
            return false;
        }
        if (cell.isEmpty()) {
            return false;
        }
        return Stream.of(getRowContainingCoords(coords), getColumnContainingCoords(coords), getFwdDiagonalContainingCoords(coords), getBckDiagonalContainingCoords(coords))
                .unordered().parallel()
                .anyMatch(cellList -> cell.isBelongingToChainOfNCellsInList(Objects.requireNonNull(N), cellList));
    }

    @NotNull
    private List<Cell> getRowContainingCoords(@NotNull final Coordinates coords) {
        return new ArrayList<>(Arrays.asList(matrix[Objects.requireNonNull(coords).getX()]));
    }

    @NotNull
    private List<Cell> getColumnContainingCoords(@NotNull final Coordinates coords) {
        Objects.requireNonNull(coords);
        return IntStream
                .range(0, getSize())
                .sequential()
                .mapToObj(x -> getCellAtCoordinatesOrNullIfInvalid(new Coordinates(x, coords.getY())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Cell> getFwdDiagonalContainingCoords(@NotNull final Coordinates coords) {
        return getDiagonalContainingCoords(coords, false);
    }

    @NotNull
    private List<Cell> getBckDiagonalContainingCoords(@NotNull final Coordinates coords) {
        return getDiagonalContainingCoords(coords, true);
    }

    @NotNull
    private List<Cell> getDiagonalContainingCoords(@NotNull final Coordinates coords, boolean isBackDiagonal) {
        int boardSize = getSize();
        int sign = isBackDiagonal ? -1 : 1;
        int S = Objects.requireNonNull(coords).getX() + sign * coords.getY();

        return IntStream.range(0, boardSize).sequential()
                .filter(i -> boardSize + sign * i > sign * S && sign * i <= sign * S)
                // sign ==  1 => boardSize + i >  S &&  i <=  S
                // sign == -1 => boardSize - i > -S && -i <= -S
                .boxed()
                .flatMap(i -> IntStream.range(0, boardSize).unordered().parallel()
                        .filter(j -> i + sign * j == S)
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(this::getCellAtCoordinatesOrNullIfInvalid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        int lengthOfMaxCoordsValue = String.valueOf(size.intValue() - 1).length();

        return String.format("  %s%" + lengthOfMaxCoordsValue + "s", lengthOfMaxCoordsValue == 1 ? " " : "", "") +
                IntStream.range(0, size.intValue())
                        .mapToObj(col -> String.format("%" + lengthOfMaxCoordsValue + "d  ", col))
                        .collect(Collectors.joining()) +
                System.lineSeparator() +
                IntStream.range(0, size.intValue())
                        .mapToObj(row ->
                                String.format("%" + lengthOfMaxCoordsValue + "d| ", row)
                                        + IntStream.range(0, size.intValue())
                                        .mapToObj(col -> {
                                            try {
                                                return String.format(" %s%" + lengthOfMaxCoordsValue + "s",
                                                        getCellAtCoordinates(row, col), "");
                                            } catch (CellOutOfBoardException e) {
                                                Utility.getLoggerOfClass(getClass()).severe(e.getMessage());
                                                throw new IllegalStateException(e);
                                            }
                                        })
                                        .collect(Collectors.joining())
                                        + System.lineSeparator())
                        .collect(Collectors.joining());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Board clone() {
        return new Board(this);
    }

    @NotNull
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

    public static class CellOutOfBoardException extends Exception {
        public CellOutOfBoardException(@NotNull final Coordinates invalidCoords) {
            super(Objects.requireNonNull(invalidCoords) + " is out of board.");
        }
    }
}