package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.utils.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class CPUPlayer extends Player {

    public final static double MIN_SKILL_FACTOR = 0.0;
    public final static double MAX_SKILL_FACTOR = 1.0;
    public final static double DEFAULT_SKILL_FACTOR = 1.0;
    private final static int DELAY_BEFORE_PLACING_STONE_MILLIS = 100;
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
        if (isValidSkillFactor(skillFactor)) {
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

    public static boolean isValidSkillFactor(final double value) {
        return value >= CPUPlayer.MIN_SKILL_FACTOR && value <= CPUPlayer.MAX_SKILL_FACTOR;
    }

    @Override
    public void makeMove() {
        Coordinates nextMoveToMake = null;
        try {
            Thread.sleep(DELAY_BEFORE_PLACING_STONE_MILLIS);
            nextMoveToMake = chooseEmptyCoordinatesSmartly();
            super.setMoveToBeMade(nextMoveToMake);
            super.makeMove();
        } catch (GameEndedException |
                CellOutOfBoardException | CellAlreadyOccupiedException e) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "Illegal move: impossible to choose coordinate " + nextMoveToMake, e);
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Thread interrupted for unknown reason.", e);
        }
    }

    @NotNull
    public Coordinates chooseEmptyCoordinatesFromCenter() {
        int boardSize = getBoardSizeInCurrentGame();
        double moreCenterValue = boardSize / 2.0 - 0.5;

        Optional<Coordinates> coords = getStreamOfEmptyCoordinatesOnBoardInCurrentGame()
                .min((coord1, coord2) ->
                        (int) (getWeightRespectToCenter(moreCenterValue, coord1)
                                - getWeightRespectToCenter(moreCenterValue, coord2)));
        if (coords.isEmpty()) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "At this point game should have already been ended");
            throw new IllegalStateException();
        }
        return coords.get();
    }

    @NotNull
    public Coordinates chooseEmptyCoordinatesSmartly() {

        final int MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED = 5;
        final int MIN_CHAIN_LENGTH_TO_FIND_INCLUDED = 2;

        if (isFirstMove() || isNaiveMove()) {
            return chooseEmptyCoordinatesFromCenter();
        }

        IntStream possibleChainLengths =
                IntStream.range(MIN_CHAIN_LENGTH_TO_FIND_INCLUDED, MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED)
                        .map(i -> MIN_CHAIN_LENGTH_TO_FIND_INCLUDED - i + MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED - 1);

        List<Coordinates> smartCoordinates = possibleChainLengths
                .boxed()
                .flatMap(chainLength -> getStreamOfEmptyCoordinatesOnBoardInCurrentGame()
                        .filter(coord -> isHeadOfAChainOfStonesInCurrentGame(coord, new PositiveInteger(chainLength))))
                .toList();

        return smartCoordinates.size() > 0 ? smartCoordinates.get(0) : chooseEmptyCoordinatesFromCenter();
    }

    private boolean isNaiveMove() {
        return rand.nextDouble() > skillFactor;
    }
}

