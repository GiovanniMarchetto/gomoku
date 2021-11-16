package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class CPUPlayer extends Player {

    private final static int DELAY_BEFORE_PLACING_STONE_MILLIS = 200;
    @NotNull
    private final static String CPU_DEFAULT_NAME = "CPU";
    @NotNull
    private final static NonNegativeInteger numberOfCpuPlayers = new NonNegativeInteger();
    @NotNull
    private final Random rand = new Random();

    // TODO : add field with difficulty (e.g., a threshold as double in [0,1]: generate random in [0,1] and if generated random value is > threshold => place stone smartly, else place stone randomly/naively

    public CPUPlayer(@NotNull String name) {
        super(name);
    }

    public CPUPlayer() {
        super(CPU_DEFAULT_NAME + numberOfCpuPlayers.incrementAndGet());
    }

    @Override
    public void makeMove() throws NoGameSetException {
        Game currentGame = getCurrentGame();
        if (currentGame != null) {
            Utility.runOnSeparateThread(() -> {//TODO: separate thread in model?
                Coordinates nextMoveToMake = null;
                try {
                    Thread.sleep(DELAY_BEFORE_PLACING_STONE_MILLIS);
                    nextMoveToMake = chooseSmartEmptyCoordinates();
                    super.setNextMove(nextMoveToMake, currentGame);
                    try {//TODO:temporary
                        super.makeMove();
                    } catch (NoGameSetException ignored) {
                    }
                } catch (BoardIsFullException | GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException e) {
                    // TODO: correctly handled exception?
                    Utility.getLoggerOfClass(getClass())
                            .log(Level.SEVERE, "Illegal move: impossible to choose coordinate " + nextMoveToMake, e);
                    throw new IllegalStateException(e);
                } catch (InterruptedException e) {
                    Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Thread interrupted for unknown reason.", e);
                }
            });
        } else {
            throw new NoGameSetException();
        }
    }

    @NotNull
    public Coordinates chooseSmartEmptyCoordinates() throws BoardIsFullException {

        if (Objects.requireNonNull(getCurrentGame()).isBoardEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter();
        }

        final int MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED = 5;
        final int MIN_CHAIN_LENGTH_TO_FIND_INCLUDED = 2;
        IntStream possibleChainLengths =
                IntStream.range(MIN_CHAIN_LENGTH_TO_FIND_INCLUDED, MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED)
                        .map(i -> MIN_CHAIN_LENGTH_TO_FIND_INCLUDED - i + MAX_CHAIN_LENGTH_TO_FIND_EXCLUDED - 1);

        List<Coordinates> smartCoordinates = possibleChainLengths
                .boxed()
                .flatMap(chainLength -> getCurrentGame().getStreamOfEmptyCoordinatesOnBoard()
                        .filter(coord -> getCurrentGame().isHeadOfAChainOfStones(coord, new PositiveInteger(chainLength))))
                .toList();

        return smartCoordinates.size() > 0 ? smartCoordinates.get(0) : chooseNextEmptyCoordinatesFromCenter();
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter() throws BoardIsFullException {
        int boardSize = Objects.requireNonNull(getCurrentGame()).getBoardSize();
        double centerValue = boardSize / 2.0 - 0.5; // TODO : why -0.5?

        return getCurrentGame().getStreamOfEmptyCoordinatesOnBoard()
                .min((coord1, coord2) ->
                        (int) (getWeightRespectToCenter(centerValue, coord1) - getWeightRespectToCenter(centerValue, coord2)))
                .orElseThrow(BoardIsFullException::new);
    }

    @NotNull
    public Coordinates chooseRandomEmptyCoordinates() throws BoardIsFullException {
        if (Objects.requireNonNull(getCurrentGame()).isThereAnyEmptyCellOnBoard()) {
            List<Coordinates> emptyCoordinates = getCurrentGame().getStreamOfEmptyCoordinatesOnBoard().toList();
            return emptyCoordinates.get(rand.nextInt(emptyCoordinates.size()));
        }
        throw new BoardIsFullException();
    }

    private double getWeightRespectToCenter(double center, Coordinates coordinates) {
        return Math.pow(Math.abs(center - coordinates.getX()), 2) + Math.pow(Math.abs(center - coordinates.getY()), 2);
    }
}

