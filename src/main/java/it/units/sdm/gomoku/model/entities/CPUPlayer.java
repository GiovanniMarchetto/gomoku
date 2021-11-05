package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.NoMoreEmptyPositionAvailableException;

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

    private static boolean isHeadOfAChainOfStones(Board board, Coordinates startCoordinates,
                                                  PositiveInteger numberOfConsecutive) {
        int[] directionFactorXs = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] directionFactorYs = {1, -1, 0, 0, 1, -1, -1, 1};

        return IntStream.range(0, directionFactorXs.length)
                .mapToObj(i -> new Pair<>(directionFactorXs[i], directionFactorYs[i]))
                .map(aDirectionFactor -> {
                            List<Stone> stoneList = IntStream.range(1, numberOfConsecutive.intValue() + 1)
                                    .mapToObj(i -> new Pair<>(
                                            startCoordinates.getX() + i * aDirectionFactor.getKey(),
                                            startCoordinates.getY() + i * aDirectionFactor.getValue()))
                                    .filter(pair -> pair.getKey() >= 0 && pair.getValue() >= 0)
                                    .map(validPair -> new Coordinates(validPair.getKey(), validPair.getValue()))
                                    .filter(board::isCoordinatesInsideBoard)
                                    .map(board::getStoneAtCoordinates)
                                    .filter(stone -> !stone.isNone())
                                    .collect(Collectors.toList());
                            if (stoneList.size() != 0) {
                                return stoneList.stream()
                                        .filter(stone -> stone == stoneList.get(0))
                                        .count();
                            } else {
                                return 0L;
                            }
                        }
                )
                .anyMatch(counter -> counter == numberOfConsecutive.intValue());
    }

    @NotNull
    public Coordinates chooseEmptyCoordinates(@NotNull Board board) throws NoMoreEmptyPositionAvailableException {

        if (board.isEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter(board);
        }

        final int maxChainToFind = 5;//exclusive
        final int minChainToFind = 2;//inclusive

        AtomicInteger consecutiveStonesToFind = new AtomicInteger(maxChainToFind);
        for (; consecutiveStonesToFind.get() >= minChainToFind; consecutiveStonesToFind.getAndDecrement()) {
            Optional<Coordinates> optionalCoordinates = IntStream.range(0, board.getSize()).boxed()
                    .flatMap(x -> IntStream.range(0, board.getSize())
                            .mapToObj(y -> new Coordinates(x, y)))
                    .filter(c -> board.getStoneAtCoordinates(c).isNone())
                    .filter(c -> isHeadOfAChainOfStones(board, c, new PositiveInteger(consecutiveStonesToFind.get())))
                    .findAny();

            if (optionalCoordinates.isPresent()) {
                return optionalCoordinates.get();
            }
        }

        return chooseNextEmptyCoordinatesFromCenter(board);
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

