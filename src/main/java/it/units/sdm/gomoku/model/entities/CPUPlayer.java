package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
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

    private static boolean isHeadOfAChainOfStones(Board board, Coordinates coordinates,
                                                  Board.Stone stoneColor,
                                                  PositiveInteger numberOfConsecutive) {
        int[] directionFactorXs = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] directionFactorYs = {1, -1, 0, 0, 1, -1, -1, 1};

        return IntStream.range(0, directionFactorXs.length)
                .mapToObj(i -> new Pair<>(directionFactorXs[i], directionFactorYs[i]))
                .map(aDirectionFactor -> IntStream.range(1, numberOfConsecutive.intValue())
                        .mapToObj(i -> new Pair<>(
                                coordinates.getX() + i * aDirectionFactor.getKey(),
                                coordinates.getY() + i * aDirectionFactor.getValue()))
                        .filter(pair -> pair.getKey() >= 0 && pair.getValue() >= 0)
                        .map(validPair -> new Coordinates(validPair.getKey(), validPair.getValue()))
                        .filter(aCoord -> board.isCoordinatesInsideBoard(aCoord) &&
                                stoneColor == board.getStoneAtCoordinates(aCoord))
                        .count()
                )
                .anyMatch(counter -> counter == numberOfConsecutive.intValue() - 1);
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

