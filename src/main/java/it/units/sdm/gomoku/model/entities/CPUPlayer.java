package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.entities.Board.BoardIsFullException;

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
    public void makeMove(@NotNull final Game currentGame) {
        Utility.runOnSeparateThread(() -> {
            Coordinates nextMoveToMake = null;
            try {
                Thread.sleep(DELAY_BEFORE_PLACING_STONE_MILLIS);
                nextMoveToMake = chooseSmartEmptyCoordinates(Objects.requireNonNull(currentGame).getBoard());
                super.setNextMove(nextMoveToMake, currentGame);
                super.makeMove(currentGame);
            } catch (BoardIsFullException | Game.GameEndedException | Board.CellOutOfBoardException | Board.CellAlreadyOccupiedException e) {
                // TODO: correctly handled exception?
                Utility.getLoggerOfClass(getClass())
                        .log(Level.SEVERE, "Illegal move: impossible to choose coordinate " + nextMoveToMake, e);
                throw new IllegalStateException(e);
            } catch (InterruptedException e) {
                Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Thread interrupted for unknown reason.", e);
            }
        });
    }

    @NotNull
    public Coordinates chooseSmartEmptyCoordinates(@NotNull Board board) throws BoardIsFullException {

        if (board.isEmpty()) {
            return chooseNextEmptyCoordinatesFromCenter(board);
        }

        final int maxChainToFind = 5;//exclusive
        final int minChainToFind = 2;//inclusive
        final int[] chainToFind = IntStream.range(minChainToFind, maxChainToFind)
                .map(i -> minChainToFind - i + maxChainToFind - 1).toArray();

        for (int i : chainToFind) {
            Optional<Coordinates> optionalCoordinates = getStreamOfEmptyCoordinates(board)
                    .filter(c -> isHeadOfAChainOfStones(board, c, new PositiveInteger(i)))
                    .findAny();
            if (optionalCoordinates.isPresent()) {
                return optionalCoordinates.get();
            }
        }
        return chooseNextEmptyCoordinatesFromCenter(board);
    }

    private static boolean isHeadOfAChainOfStones(Board board, Coordinates headCoordinates,
                                                  PositiveInteger numberOfConsecutive) {    // TODO: too many responsibilities?
        return IntStream.rangeClosed(-1, 1).mapToObj(xDirection ->
                        IntStream.rangeClosed(-1, 1).mapToObj(yDirection ->
                                        IntStream.rangeClosed(1, numberOfConsecutive.intValue())
                                                .mapToObj(i -> new Pair<>(
                                                        headCoordinates.getX() + i * xDirection,
                                                        headCoordinates.getY() + i * yDirection))
                                                .filter(pair -> pair.getKey() >= 0 && pair.getValue() >= 0)
                                                .map(validPair -> new Coordinates(validPair.getKey(), validPair.getValue()))
                                                .filter(board::isCoordinatesInsideBoard)
                                                .map(board::getCellAtCoordinatesOrNullIfInvalid)
                                                .filter(Objects::nonNull)
                                                .filter(cell -> !cell.isEmpty())
                                                .collect(Collectors.groupingBy(Cell::getStone, Collectors.counting()))
                                                .values()
                                                .stream()
                                                .anyMatch(counter -> counter == numberOfConsecutive.intValue()))
                                .anyMatch(find -> find))
                .anyMatch(find -> find);
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinates(@NotNull Board board) throws BoardIsFullException {
        if (board.isThereAnyEmptyCell()) {
            //noinspection OptionalGetWithoutIsPresent because is yet checked with isAnyEmptyPosition
            return getStreamOfEmptyCoordinates(board).findFirst().get();
        }
        throw new BoardIsFullException();
    }

    @NotNull
    public Coordinates chooseNextEmptyCoordinatesFromCenter(@NotNull Board board) throws BoardIsFullException {
        int boardSize = board.getSize();
        double centerValue = boardSize / 2.0 - 0.5;

        if (board.isThereAnyEmptyCell()) {
            //noinspection OptionalGetWithoutIsPresent // because is yet checked with isThereAnyEmptyCell
            return getStreamOfEmptyCoordinates(board).min((o1, o2) ->
                    (int) (getWeightRespectToCenter(centerValue, o1) - getWeightRespectToCenter(centerValue, o2))).get();
        }
        throw new BoardIsFullException();
    }

    @NotNull
    public Coordinates chooseRandomEmptyCoordinates(@NotNull Board board) throws BoardIsFullException {
        if (board.isThereAnyEmptyCell()) {
            List<Coordinates> emptyCoordinates = getStreamOfEmptyCoordinates(board).toList();
            return emptyCoordinates.get(rand.nextInt(emptyCoordinates.size()));
        }
        throw new BoardIsFullException();
    }

    private double getWeightRespectToCenter(double center, Coordinates coordinates) {
        return Math.pow(Math.abs(center - coordinates.getX()), 2) + Math.pow(Math.abs(center - coordinates.getY()), 2);
    }

    @NotNull
    private Stream<Coordinates> getStreamOfEmptyCoordinates(@NotNull Board board) {
        return IntStream.range(0, board.getSize()).boxed()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(c -> {
                    try {
                        return board.getCellAtCoordinates(c).isEmpty();
                    } catch (Board.CellOutOfBoardException e) {
                        return false;
                    }
                });
    }
}

