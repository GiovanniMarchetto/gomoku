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

    public static final String BoardMatrixPropertyName = "matrix";
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

    public static boolean checkNConsecutiveStonesNaive(Board board, Coordinates coordinates, PositiveInteger numberOfConsecutiveStoneForWinning) {

        int[] factorsCols = new int[]{0, 1, 0, -1};
        if (verifyDirection(board, coordinates, numberOfConsecutiveStoneForWinning, factorsCols)) return true;

        int[] factorsRows = new int[]{1, 0, -1, 0};
        if (verifyDirection(board, coordinates, numberOfConsecutiveStoneForWinning, factorsRows)) return true;

        int[] factorsRightDiag = new int[]{1, 1, -1, -1};
        if (verifyDirection(board, coordinates, numberOfConsecutiveStoneForWinning, factorsRightDiag)) return true;

        int[] factorsLeftDiag = new int[]{1, -1, -1, 1};
        return verifyDirection(board, coordinates, numberOfConsecutiveStoneForWinning, factorsLeftDiag);
    }

    private static boolean verifyDirection(Board board, Coordinates coordinates, PositiveInteger numberOfConsecutiveStoneForWinning, int[] factors) {

        Board.Stone stoneColor = board.getStoneAtCoordinates(coordinates);

        int partRight = getNumberOfConsecutiveSameColoreStones(board, coordinates, numberOfConsecutiveStoneForWinning, stoneColor, factors[0], factors[1]); //up-right

        int partLeft = getNumberOfConsecutiveSameColoreStones(board, coordinates, numberOfConsecutiveStoneForWinning, stoneColor, factors[2], factors[3]); //down-left

        return (partRight + partLeft + 1) >= numberOfConsecutiveStoneForWinning.intValue();
    }

    private static int getNumberOfConsecutiveSameColoreStones(Board board, Coordinates coordinates, PositiveInteger numberOfConsecutiveStoneForWinning, Board.Stone stoneColor, int factorX, int factorY) {
        int consecutive = 0;

        for (int i = 1; i < numberOfConsecutiveStoneForWinning.intValue(); i++) {
            int x = coordinates.getX() + i * factorX;
            int y = coordinates.getY() + i * factorY;
            if (x >= 0 && y >= 0) {
                Coordinates currentCoordinates = new Coordinates(x, y);
                if (board.isCoordinatesInsideBoard(currentCoordinates) &&
                        stoneColor == board.getStoneAtCoordinates(currentCoordinates)) {
                    consecutive += 1;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return consecutive;
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

    public synchronized boolean isAnyEmptyPositionOnTheBoard() {
        return numberOfFilledPositionOnTheBoard.intValue() < Math.pow(size.intValue(), 2);
    }

    public synchronized void occupyPosition(@NotNull Board.Stone stone, @NotNull Coordinates coordinates)
            throws NoMoreEmptyPositionAvailableException, PositionAlreadyOccupiedException {
        if (isAnyEmptyPositionOnTheBoard()) {
            if (isCoordinatesEmpty(Objects.requireNonNull(coordinates))) {
                setStoneAtCoordinates(coordinates, Objects.requireNonNull(stone));
                numberOfFilledPositionOnTheBoard.incrementAndGet();
                firePropertyChange(BoardMatrixPropertyName, null, new ChangedCell(coordinates, stone, this));
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

    @Override
    public Board clone() {
        return new Board(this);
    }

    @NotNull
    private List<Stone> fwdDiagonalToList(@NotNull final Coordinates coords) {
        int B = getSize();
        int S = Objects.requireNonNull(coords).getX() + coords.getY();

        var list = IntStream.range(0, B).sequential()
                .filter(i -> B + i > S && i <= S)
                .boxed()
                .flatMap(i -> IntStream.range(0, B).unordered()
                        .filter(j -> i + j == S)
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(this::getStoneAtCoordinates)
                .collect(Collectors.toList());


        int x = Math.min(S, B - 1);
        int y = Math.max(S - (B - 1), 0);
        ArrayList<Stone> arr = new ArrayList<>();
        while (y < B && x >= 0) {
            arr.add(matrix[x][y]);
            x--;
            y++;
        }
        return list;
    }

    @NotNull
    private List<Stone> bckDiagonalToList(@NotNull final Coordinates coords) {
        int B = getSize();
        int S = Objects.requireNonNull(coords).getX() - coords.getY();


        var list = IntStream.range(0, B).sequential()
                .filter(i -> B - i > -S && i >= S)
                .boxed()
                .flatMap(i -> IntStream.range(0, B).unordered()
                        .filter(j -> i - j == S)
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(this::getStoneAtCoordinates)
                .collect(Collectors.toList());

        int x = Math.max(S, 0);
        int y = -Math.min(S, 0);
        ArrayList<Stone> arr = new ArrayList<>();
        while (x < B && y < B) {
            arr.add(matrix[x][y]);
            x++;
            y++;
        }
        return list;
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
