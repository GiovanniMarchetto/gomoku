package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class CPUPlayer extends Player {

    public final static double MIN_SKILL_FACTOR = 0.0;
    public final static double MAX_SKILL_FACTOR = 1.0;
    public final static double DEFAULT_SKILL_FACTOR = 1.0;
    private final static int DELAY_BEFORE_PLACING_STONE_MILLIS = 200;
    @NotNull
    private final static String CPU_DEFAULT_NAME = "CPU";
    @NotNull
    private final static NonNegativeInteger numberOfCpuPlayers = new NonNegativeInteger();
    private final double skillFactor;
    @NotNull
    private final Random rand = new Random();

    public CPUPlayer(@NotNull String name,
                     @Range(from = (int) MIN_SKILL_FACTOR, to = (int) MAX_SKILL_FACTOR) double skillFactor)
            throws IllegalArgumentException {
        super(name);
        if (skillFactor >= MIN_SKILL_FACTOR && skillFactor <= MAX_SKILL_FACTOR) {
            this.skillFactor = skillFactor;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public CPUPlayer(@NotNull String name) {
        this(name, DEFAULT_SKILL_FACTOR);
    }

    public CPUPlayer() {
        this(CPU_DEFAULT_NAME + numberOfCpuPlayers.incrementAndGet());
    }

    private static double getWeightRespectToCenter(double center, Coordinates coordinates) {
        return Math.pow(center - coordinates.getX(), 2) + Math.pow(center - coordinates.getY(), 2);
    }

    public static boolean isValidSkillFactorFromString(@NotNull final String value) {
        try {
            double input = Double.parseDouble(value);
            return input >= CPUPlayer.MIN_SKILL_FACTOR && input <= CPUPlayer.MAX_SKILL_FACTOR;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void makeMove() {
        Coordinates nextMoveToMake = null;
        try {
            Thread.sleep(DELAY_BEFORE_PLACING_STONE_MILLIS);
            nextMoveToMake = chooseSmartEmptyCoordinates();
            super.setMoveToBeMade(nextMoveToMake);
            super.makeMove();
        } catch (BoardIsFullException | GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException e) {
            // TODO: correctly handled exception?
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "Illegal move: impossible to choose coordinate " + nextMoveToMake, e);
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Thread interrupted for unknown reason.", e);
        }
    }

    @NotNull
    public Coordinates chooseSmartEmptyCoordinates() throws BoardIsFullException {

        final int MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED = 5;
        final int MIN_CHAIN_LENGTH_TO_FIND_INCLUDED = 2;

        if (isFirstMove() || isNaiveMove()) {
            return chooseNextEmptyCoordinatesFromCenter();
        }

        IntStream possibleChainLengths =
                IntStream.range(MIN_CHAIN_LENGTH_TO_FIND_INCLUDED, MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED)
                        .map(i -> MIN_CHAIN_LENGTH_TO_FIND_INCLUDED - i + MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED - 1);

        List<Coordinates> smartCoordinates = possibleChainLengths
                .boxed()
                .flatMap(chainLength -> getStreamOfEmptyCoordinatesOnBoardInCurrentGame()
                        .filter(coord -> isHeadOfAChainOfStonesInCurrentGame(coord, new PositiveInteger(chainLength))))
                .toList();

        return smartCoordinates.size() > 0 ? smartCoordinates.get(0) : chooseNextEmptyCoordinatesFromCenter();
    }

    private boolean isNaiveMove() {
        return rand.nextDouble() > skillFactor;
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter() throws BoardIsFullException {
        int boardSize = getBoardSizeInCurrentGame();
        double moreCenterValue = boardSize / 2.0 - 0.5;

        return getStreamOfEmptyCoordinatesOnBoardInCurrentGame()
                .min((coord1, coord2) ->
                        (int) (getWeightRespectToCenter(moreCenterValue, coord1) - getWeightRespectToCenter(moreCenterValue, coord2)))
                .orElseThrow(BoardIsFullException::new);
    }
}

