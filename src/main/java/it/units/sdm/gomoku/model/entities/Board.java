package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.NonNegativeIntegerType;
import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Board implements Serializable {

    @NotNull
    private final PositiveInteger size;
    @NotNull
    private final Cell[][] matrix;
    @NotNull
    public final Function<Stream<Stream<Coordinates>>, Stream<Stream<Cell>>> mapCoordStreamStreamToCellStreamStream =
            streamStream -> streamStream
                    .map(coordStream -> coordStream
                            .map(this::getCellAtCoordinatesOrNullIfInvalid)
                            .filter(Objects::nonNull));
    @NotNull
    private final ObservablePropertySettable<Coordinates> lastMoveCoordinatesProperty;
    private int numberOfFilledPositions;

    public Board(@NotNull PositiveInteger size) {
        this.size = size;
        this.numberOfFilledPositions = 0;
        this.matrix = IntStream.range(0, size.intValue())
                .mapToObj(i -> IntStream.range(0, size.intValue()).mapToObj(j -> new Cell()).toArray(Cell[]::new))
                .toArray(Cell[][]::new);
        this.lastMoveCoordinatesProperty = new ObservablePropertySettable<>();
    }

    public Board(@NotNull final Board board) {
        this.size = new PositiveInteger(board.size);
        this.numberOfFilledPositions = board.numberOfFilledPositions;
        this.matrix = board.getBoardMatrixCopy();
        this.lastMoveCoordinatesProperty = board.lastMoveCoordinatesProperty.clone();
    }

    @NotNull
    public static IntStream groupCellStreamStreamByStoneToIntStreamOfMaxNumberOfSameColorStones(
            @NotNull final Stream<Stream<Cell>> streamStream) {
        return streamStream
                .map(cellStream -> cellStream
                        .filter(cell -> !cell.isEmpty())
                        .collect(Collectors.groupingBy(Cell::getStone, Collectors.counting())))
                .filter(map -> map.size() > 0)
                .map(map -> map.values().stream()
                        .mapToInt(Math::toIntExact)
                        .max()
                        .orElseThrow(IllegalStateException::new))
                .mapToInt(i -> i);
    }

    @PositiveIntegerType
    public int getSize() {
        return size.intValue();
    }

    @NotNull
    public ObservableProperty<Coordinates> getLastMoveCoordinatesProperty() {
        return new ObservablePropertyProxy<>(lastMoveCoordinatesProperty);
    }

    public boolean isEmpty() {
        return numberOfFilledPositions == 0;
    }

    public boolean isThereAnyEmptyCell() {
        return numberOfFilledPositions < Math.pow(size.intValue(), 2);
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

    public synchronized void occupyPosition(@NotNull Color stoneColor, @NotNull Coordinates coordinates)
            throws BoardIsFullException, CellAlreadyOccupiedException, CellOutOfBoardException {
        if (isThereAnyEmptyCell()) {
            if (isCellEmptyAtCoordinates(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, Objects.requireNonNull(stoneColor));
                numberOfFilledPositions++;
                lastMoveCoordinatesProperty.setPropertyValue(coordinates);
            } else {
                throw new CellAlreadyOccupiedException(coordinates);
            }
        } else {
            throw new BoardIsFullException();
        }
    }

    public boolean isCellEmptyAtCoordinates(@NotNull final Coordinates coordinates) throws CellOutOfBoardException {
        return getCellAtCoordinates(Objects.requireNonNull(coordinates)).isEmpty();
    }

    private synchronized void setStoneAtCoordinates(@NotNull final Coordinates coordinates, @NotNull Color stoneColor)
            throws CellOutOfBoardException {
        if (isCoordinatesInsideBoard(Objects.requireNonNull(coordinates))) {
            getCellAtCoordinates(coordinates).setStoneFromColor(stoneColor);
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

    @NotNull
    private Cell[][] getBoardMatrixCopy() {
        return Arrays.stream(matrix).sequential()
                .map(rowOfCells -> Arrays.stream(rowOfCells).sequential().map(Cell::new).toArray(Cell[]::new))
                .toArray(Cell[][]::new);
    }

    @NotNull
    public Stream<Coordinates> getStreamOfEmptyCoordinates() {
        return IntStream.range(0, getSize()).boxed()
                .unordered().parallel()
                .flatMap(x -> IntStream.range(0, getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(c -> {
                    try {
                        return getCellAtCoordinates(c).isEmpty();
                    } catch (CellOutOfBoardException e) {
                        return false;
                    }
                });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board otherBoard = (Board) o;
        return size.equals(otherBoard.size)
                && numberOfFilledPositions == otherBoard.numberOfFilledPositions
                && lastMoveCoordinatesProperty.valueEquals(otherBoard.lastMoveCoordinatesProperty)
                && Arrays.deepEquals(matrix, otherBoard.matrix);
    }

    @Override
    public int hashCode() {
        int result = size.hashCode();
        result = 31 * result + numberOfFilledPositions;
        result = 31 * result + Arrays.deepHashCode(matrix);
        result = 31 * result + lastMoveCoordinatesProperty.hashCode();
        return result;
    }
}