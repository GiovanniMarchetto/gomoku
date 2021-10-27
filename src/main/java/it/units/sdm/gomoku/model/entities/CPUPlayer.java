package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.*;

public class CPUPlayer extends Player {

    private final static String CPU_DEFAULT_NAME = "CPU";
    private final static NonNegativeInteger numberOfCpuPlayers = new NonNegativeInteger();
    private final Random rand = new Random();

    public CPUPlayer(@NotNull String name) {
        super(name);
    }

    public CPUPlayer() {
        super(CPU_DEFAULT_NAME + numberOfCpuPlayers.incrementAndGet());
    }
//
//    private static boolean isHeadOfAChainOfStones(Board board, Coordinates coordinates,
//                                                  Board.Stone stoneColor,
//                                                  int factorX, int factorY,
//                                                  NonNegativeInteger numberOfConsecutive) {
//        for (int i = 1; i < numberOfConsecutive.intValue(); i++) {
//            int x = coordinates.getX() + i * factorX;
//            int y = coordinates.getY() + i * factorY;
//            if (x >= 0 && y >= 0) {
//                Coordinates currentCoordinates = new Coordinates(x, y);
//                if (!board.isCoordinatesInsideBoard(currentCoordinates) ||
//                        stoneColor != board.getStoneAtCoordinates(currentCoordinates)) {
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    private boolean findCorrectCoordinates(@NotNull Board board, AtomicReference<Coordinates> finalC, int i2) {
//        Board.Stone[] stoneColors = new Board.Stone[]{Board.Stone.WHITE, Board.Stone.BLACK};
//        int[] factorXs = {0, 0, 1, -1, 1, -1, 1, -1};
//        int[] factorYs = {1, -1, 0, 0, 1, -1, -1, 1};
//
//
//        for (Board.Stone stoneColor : stoneColors) {
//            IntStream.range(0, factorXs.length).forEach(i -> {
//                List<Coordinates> coordinatesList =
//                        IntStream.range(0, board.getSize()).boxed()
//                                .flatMap(x -> IntStream.range(0, board.getSize())
//                                        .mapToObj(y -> new Coordinates(x, y)))
//                                .filter(c -> board.getStoneAtCoordinates(c).isNone())
//                                .filter(c -> isHeadOfAChainOfStones(board, c, stoneColor, factorXs[i], factorYs[i], new NonNegativeInteger(i2)))
//                                .limit(1)
//                                .collect(Collectors.toList());
//                if (coordinatesList.size() != 0) {
//                    finalC.set(coordinatesList.get(0));
//                }
//            });
//            if (finalC.get() != null) {
//                return true;
//            }
//        }
//        return false;
//    }

//    @NotNull
//    public Coordinates chooseEmptyCoordinates(@NotNull Board board) throws Board.NoMoreEmptyPositionAvailableException {
//
//
//        if (board.isEmpty()) {
//            return chooseNextEmptyCoordinatesFromCenter(board);
//        }
//
//        AtomicReference<Coordinates> finalC = new AtomicReference<>();
//
//        if (findCorrectCoordinates(board, finalC, 5)) {
//            return finalC.get();
//        }
//
//        if (findCorrectCoordinates(board, finalC, 4)) {
//            return finalC.get();
//        }
//
//        if (findCorrectCoordinates(board, finalC, 3)) {
//            return finalC.get();
//        }
//
//        return chooseNextEmptyCoordinatesFromCenter(board);
//    }


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

    @NotNull
    public Coordinates chooseEmptyCoordinates(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {

        if (board.isEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter(board);
        }

        Stone[] stoneColors = new Stone[]{Stone.WHITE, Stone.BLACK};
        List<Coordinates> coordinatesList;
        for (int i = 5; i > 2; i--) {
            for (Stone stoneColor : stoneColors) {
                coordinatesList = findCoordinates(board, stoneColor,i);
                if (coordinatesList.size()!=0){
                    return coordinatesList.get(0);
                }
            }
        }

        return chooseNextEmptyCoordinatesFromCenter(board);

    }

    private List<Coordinates> findCoordinates(@NotNull Board board, Stone stoneColor,int i) {
        return IntStream.range(0, board.getSize()).boxed()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(c -> board.getStoneAtCoordinates(c).isNone())
                .filter(c -> {
                    try {
                        Board boardCopy = board.clone();
                        boardCopy.occupyPosition(stoneColor, c);
                        return checkNConsecutiveStonesNaive(boardCopy,c,new PositiveInteger(i));
                    } catch (NoMoreEmptyPositionAvailableException | PositionAlreadyOccupiedException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .limit(1)
                .collect(Collectors.toList());
    }


    @NotNull
    public Coordinates chooseNextEmptyCoordinates(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {
        if (board.isAnyEmptyPositionOnTheBoard()) {
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    var coords = new Coordinates(i, j);
                    if (board.getStoneAtCoordinates(coords) == Stone.NONE) return coords;
                }
            }
        }
        throw new NoMoreEmptyPositionAvailableException();
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {
        int s = board.getSize();
        int n = (int) Math.ceil(s / 2.0) - 1;
        if (board.isAnyEmptyPositionOnTheBoard()) {
            for (int i = n; i >= 0; i--) {
                for (int x = i; x < s - i; x++) {
                    for (int y = i; y < s - i; y++) {
                        var coords = new Coordinates(x, y);
                        if (board.getStoneAtCoordinates(coords) == Stone.NONE) return coords;
                    }
                }
            }
        }
        throw new NoMoreEmptyPositionAvailableException();
    }

    @NotNull
    public Coordinates chooseRandomEmptyCoordinates(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {
        if (board.isAnyEmptyPositionOnTheBoard()) {
            int MAX_RANDOM_ITERATION = 500;
            for (int i = 0; i < MAX_RANDOM_ITERATION; i++) {
                Coordinates coordinates = generateRandomCoordinates(board.getSize());
                if (board.getStoneAtCoordinates(coordinates).isNone())
                    return coordinates;
            }
        }
        return chooseNextEmptyCoordinates(board);
    }

    @NotNull
    public Coordinates generateRandomCoordinates(@PositiveInteger.PositiveIntegerType int boardSize) {
        return new Coordinates(rand.nextInt(boardSize), rand.nextInt(boardSize));
    }


}

