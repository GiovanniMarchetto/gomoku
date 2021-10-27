package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.NoMoreEmptyPositionAvailableException;
import static it.units.sdm.gomoku.model.entities.Board.Stone;

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

    @NotNull
    public Coordinates chooseEmptyCoordinates(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {

        if (board.isEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter(board);
        }

        Stone[] stoneColors = new Stone[]{Stone.WHITE, Stone.BLACK};
        Optional<Coordinates> optionalCoordinates;
        for (int i = 5; i > 2; i--) {
            for (Stone stoneColor : stoneColors) {
                optionalCoordinates = findCoordinates(board, stoneColor, i);
                if (optionalCoordinates.isPresent()) {
                    return optionalCoordinates.get();
                }
            }
        }

        return chooseNextEmptyCoordinatesFromCenter(board);

    }

    private Optional<Coordinates> findCoordinates(@NotNull Board board, Stone stoneColor, int i) {
        return IntStream.range(0, board.getSize()).boxed()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(c -> board.getStoneAtCoordinates(c).isNone())
                .filter(c -> isHeadOfAChainOfStones(board, c, stoneColor, new PositiveInteger(i)))
                .findAny();
    }


    private static boolean isHeadOfAChainOfStones(Board board, Coordinates coordinates,
                                                  Board.Stone stoneColor,
                                                  PositiveInteger numberOfConsecutive) {
        int[] factorXs = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] factorYs = {1, -1, 0, 0, 1, -1, -1, 1};
        int directionsFromAPoint = factorXs.length;

        for (int f = 0; f < directionsFromAPoint; f++) {
            boolean ok = true;

            for (int i = 1; i < numberOfConsecutive.intValue(); i++) {
                int x = coordinates.getX() + i * factorXs[f];
                int y = coordinates.getY() + i * factorYs[f];
                if (x >= 0 && y >= 0) {
                    Coordinates currentCoordinates = new Coordinates(x, y);
                    if (!board.isCoordinatesInsideBoard(currentCoordinates) ||
                            stoneColor != board.getStoneAtCoordinates(currentCoordinates)) {
                        ok = false;
                        break;
                    }
                } else {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return true;
            }
        }
        return false;
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

