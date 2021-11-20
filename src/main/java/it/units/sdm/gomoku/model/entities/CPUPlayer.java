package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class CPUPlayer extends Player {

    private final static int DELAY_BEFORE_PLACING_STONE_MILLIS = 200;
    private final static int DEFAULT_NAIVETY = 0;
    @NotNull
    private final static String CPU_DEFAULT_NAME = "CPU";
    @NotNull
    private final static NonNegativeInteger numberOfCpuPlayers = new NonNegativeInteger();
    private final double naivety;
    @NotNull
    private final Random rand = new Random();

    public CPUPlayer(@NotNull String name, double naivety) {
        super(name);
        this.naivety = naivety;
    }

    public CPUPlayer(@NotNull String name) {
        this(name, DEFAULT_NAIVETY);
    }

    public CPUPlayer() {
        this(CPU_DEFAULT_NAME + numberOfCpuPlayers.incrementAndGet());
    }

    private static double getWeightRespectToCenter(int center, Coordinates coordinates) {
        return Math.pow(center - coordinates.getX(), 2) + Math.pow(center - coordinates.getY(), 2);
    }

    @Override
    public void makeMove() throws NoGameSetException {
        Coordinates nextMoveToMake = null;
        try {
            Thread.sleep(DELAY_BEFORE_PLACING_STONE_MILLIS);
            nextMoveToMake = chooseSmartEmptyCoordinates();
            super.setNextMove(nextMoveToMake);
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

        if (isFirstMove() || rand.nextDouble() < naivety) {
            return chooseNextEmptyCoordinatesFromCenter();
        }

        IntStream possibleChainLengths =
                IntStream.range(MIN_CHAIN_LENGTH_TO_FIND_INCLUDED, MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED)
                        .map(i -> MIN_CHAIN_LENGTH_TO_FIND_INCLUDED - i + MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED - 1);

        List<Coordinates> smartCoordinates = possibleChainLengths
                .boxed()
                .flatMap(chainLength -> getStreamOfEmptyCoordinatesOnBoard()
                        .filter(coord -> isHeadOfAChainOfStones(coord, new PositiveInteger(chainLength))))
                .toList();

        return smartCoordinates.size() > 0 ? smartCoordinates.get(0) : chooseNextEmptyCoordinatesFromCenter();
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter() throws BoardIsFullException {
        int boardSize = getBoardSize();
        int moreCenterValue = (int) Math.ceil(boardSize / 2.0) - 1;

        return getStreamOfEmptyCoordinatesOnBoard()
                .min((coord1, coord2) ->
                        (int) (getWeightRespectToCenter(moreCenterValue, coord1) - getWeightRespectToCenter(moreCenterValue, coord2)))
                .orElseThrow(BoardIsFullException::new);
    }
}

