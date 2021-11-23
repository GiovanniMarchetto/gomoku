package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.game.GameTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTestUtility {

    @NotNull
    public static Game createNewGameWithDefaultParams() {
        return new Game(BOARD_SIZE, blackPlayer, whitePlayer);
    }

    public static void disputeGameWithSmartAlgorithm(Game game) throws GameNotStartedException {
        final CPUPlayer cpuPlayer = new CPUPlayer();
        cpuPlayer.setCurrentGame(game);
        while (!game.isEnded()) {
            try {
                game.placeStoneAndChangeTurn(cpuPlayer.chooseEmptyCoordinatesSmartly());
            } catch (CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
                Assertions.fail("During disputeGameWithSmartAlgorithm throw: " + e);
            }
        }
    }

    public static void disputeGameAndDraw(Game game) {
        try {
            List<Coordinates> remainCoordinates = new ArrayList<>();
            int boardSize = game.getBoardSize();
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    if (x % 3 == 0 && y == 0) {
                        game.placeStoneAndChangeTurn(new Coordinates(x, y));
                    } else {
                        remainCoordinates.add(new Coordinates(x, y));
                    }
                }
            }

            for (Coordinates c : remainCoordinates) {
                game.placeStoneAndChangeTurn(c);
            }
        } catch (GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException | GameNotStartedException e) {
            Assertions.fail("During disputeGameAndDraw throw: " + e);
        }
    }

    public static void disputeGameAndMakeThePlayerToWin(Game game, Player player) {
        try {
            placeTwoChainOfFourIn0And1Rows(game);

            if (player == game.getCurrentPlayerProperty().getPropertyValue()) {
                game.placeStoneAndChangeTurn(new Coordinates(0, 4));
            } else {
                game.placeStoneAndChangeTurn(new Coordinates(2, 0));
                game.placeStoneAndChangeTurn(new Coordinates(1, 4));
            }
        } catch (GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException | GameNotStartedException e) {
            Assertions.fail("During disputeGameAndMakeThePlayerToWin throw: " + e);
        }
    }

    static void placeTwoChainOfFourIn0And1Rows(Game game)
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException {

        for (int i = 0; i < 4; i++) {
            game.placeStoneAndChangeTurn(new Coordinates(0, i));
            game.placeStoneAndChangeTurn(new Coordinates(1, i));
        }
    }


    public static Game createAndDisputeGameFromCsv(Cell[][] cellMatrix, Coordinates coordinatesToControl)
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException, GameAlreadyStartedException {

        Game game = new Game(new PositiveInteger(cellMatrix.length), new CPUPlayer(), new CPUPlayer());
        game.start();

        Board board = TestUtility.createBoardFromCellMatrix(cellMatrix);

        List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(board, Color.BLACK);
        blackCoordinatesList.remove(coordinatesToControl);
        List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(board, Color.WHITE);
        whiteCoordinatesList.remove(coordinatesToControl);

        for (int i = 0; i < blackCoordinatesList.size(); i++) {
            game.placeStoneAndChangeTurn(blackCoordinatesList.get(i));
            if (whiteCoordinatesList.size() == i) {
                break;
            }
            game.placeStoneAndChangeTurn(whiteCoordinatesList.get(i));
        }
        game.placeStoneAndChangeTurn(coordinatesToControl);

        return game;
    }

    @NotNull
    private static List<Coordinates> getListOfCoordinatesOfAColor(Board board, Color color) throws CellOutOfBoardException {

        AtomicReference<CellOutOfBoardException> eventuallyThrownException = new AtomicReference<>();

        List<Coordinates> coordinatesList = IntStream.range(0, board.getSize())
                .boxed().sequential()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(coords -> {
                    try {
                        //noinspection ConstantConditions //the empty cells are filtered before the color request
                        return !board.getCellAtCoordinates(coords).isEmpty() &&
                                board.getCellAtCoordinates(coords).getStone().getColor() == color;
                    } catch (CellOutOfBoardException e) {
                        eventuallyThrownException.set(e);
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (eventuallyThrownException.get() != null) {
            throw eventuallyThrownException.get();
        }

        return coordinatesList;
    }

    @Nested
    class GameTestUtilityTest {
        @ParameterizedTest
        @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
        void countIfStonesOfDifferentColorsAreTheSameOrBlackHaveOneMoreStoneOnTheBoard(Cell[][] cellMatrix, Coordinates coordinatesToControl)
                throws GameAlreadyStartedException, CellOutOfBoardException {
            Game game = new Game(new PositiveInteger(cellMatrix.length), new CPUPlayer(), new CPUPlayer());
            game.start();

            Board board = TestUtility.createBoardFromCellMatrix(cellMatrix);

            List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(board, Color.BLACK);
            List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(board, Color.WHITE);

            if ((blackCoordinatesList.size() == whiteCoordinatesList.size() && whiteCoordinatesList.contains(coordinatesToControl)) ||
                    (blackCoordinatesList.size() == whiteCoordinatesList.size() + 1 && blackCoordinatesList.contains(coordinatesToControl))) {
                return;
            }
            fail("The construction of the game in the csv is wrong! ");
        }

        @ParameterizedTest
        @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
        void createAndDisputeGameAndCheckIfTheResultBoardOfGameIsTheSameOfTheOneProvided(Cell[][] matrix, Coordinates coordinatesToControl)
                throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException, GameAlreadyStartedException {

            Game game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
            Board expectedBoard = TestUtility.createBoardFromCellMatrix(matrix);
            assertEquals(expectedBoard.toString(), game.getBoard().toString());
        }
    }
}
