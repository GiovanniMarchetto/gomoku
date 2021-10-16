package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CPUPlayer extends Player {

    private final static String CPU_DEFAULT_NAME = "CPU";
    private final static NonNegativeInteger numberOfCpuPlayers = new NonNegativeInteger();
    private final int MAX_RANDOM_ITERATION = 500;
    private final Random rand = new Random();

    public CPUPlayer(@NotNull String name) {
        super(name);
    }

    public CPUPlayer() {
        super(CPU_DEFAULT_NAME + numberOfCpuPlayers.incrementAndGet());
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

