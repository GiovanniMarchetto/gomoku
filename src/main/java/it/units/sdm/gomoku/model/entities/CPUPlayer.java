package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

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

    private static boolean isHeadOfAChainOfStones(Board board, Coordinates coordinates,
                                                  Board.Stone stoneColor,
                                                  int factorX, int factorY,
                                                  NonNegativeInteger numberOfConsecutive) {
        for (int i = 1; i < numberOfConsecutive.intValue(); i++) {
            int x = coordinates.getX() + i * factorX;
            int y = coordinates.getY() + i * factorY;
            if (x >= 0 && y >= 0) {
                Coordinates currentCoordinates = new Coordinates(x, y);
                if (!board.isCoordinatesInsideBoard(currentCoordinates) ||
                        stoneColor != board.getStoneAtCoordinates(currentCoordinates)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    private void findCorrectCoordinates(@NotNull Board board, AtomicReference<Coordinates> finalC, int i2) {
        Board.Stone[] stoneColors = new Board.Stone[]{Board.Stone.WHITE, Board.Stone.BLACK};
        int[] factorXs = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] factorYs = {1, -1, 0, 0, 1, -1, -1, 1};

        for (Board.Stone stoneColor : stoneColors) {
            IntStream.range(0, board.getSize()).forEach(x ->
                    IntStream.range(0, board.getSize()).forEach(y -> {
                                Coordinates coordinates = new Coordinates(x, y);
                                if (board.getStoneAtCoordinates(coordinates).isNone()) {

                                    IntStream.range(0, factorXs.length).forEach(i -> {
                                        if (isHeadOfAChainOfStones(board, coordinates, stoneColor,
                                                factorXs[i], factorYs[i],
                                                new NonNegativeInteger(i2))) {
                                            finalC.set(new Coordinates(x, y));
                                        }
                                    });

                                }
                            }
                    )
            );
        }
    }

    @NotNull
    public Coordinates chooseEmptyCoordinates(@NotNull Board board) throws Board.NoMoreEmptyPositionAvailableException {

        if (board.isEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter(board);
        }

        AtomicReference<Coordinates> finalC = new AtomicReference<>();

        findCorrectCoordinates(board, finalC, 5);
        if (finalC.get()!=null){
            return finalC.get();
        }

        findCorrectCoordinates(board, finalC, 4);

        if (finalC.get()!=null){
            return finalC.get();
        }

//        for (int x = 0; x < board.getSize(); x++) {
//            for (int y = 0; y < board.getSize(); y++) {
//                Coordinates coordinates = new Coordinates(x, y);
//                if (board.getStoneAtCoordinates(coordinates).isNone()) {
//                    for (Board.Stone stoneColor : stoneColors) {
//                        for (int i = 0; i < factorXs.length; i++) {
//                            if (isHeadOfAChainOfStones(board, coordinates, stoneColor,
//                                    factorXs[i], factorYs[i],
//                                    new NonNegativeInteger(4))) {
//                                return new Coordinates(x, y);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        return chooseNextEmptyCoordinatesFromCenter(board);
    }


    @NotNull
    public Coordinates chooseNextEmptyCoordinates(@NotNull Board board) throws Board.NoMoreEmptyPositionAvailableException {
        if (board.isAnyEmptyPositionOnTheBoard()) {
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    var coords = new Coordinates(i, j);
                    if (board.getStoneAtCoordinates(coords) == Board.Stone.NONE) return coords;
                }
            }
        }
        throw new Board.NoMoreEmptyPositionAvailableException();
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter(@NotNull Board board) throws Board.NoMoreEmptyPositionAvailableException {
        int s = board.getSize();
        int n = (int) Math.ceil(s / 2.0) - 1;
        if (board.isAnyEmptyPositionOnTheBoard()) {
            for (int i = n; i >= 0; i--) {
                for (int x = i; x < s - i; x++) {
                    for (int y = i; y < s - i; y++) {
                        var coords = new Coordinates(x, y);
                        if (board.getStoneAtCoordinates(coords) == Board.Stone.NONE) return coords;
                    }
                }
            }
        }
        throw new Board.NoMoreEmptyPositionAvailableException();
    }

    @NotNull
    public Coordinates chooseRandomEmptyCoordinates(@NotNull Board board) throws Board.NoMoreEmptyPositionAvailableException {
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

