package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.gui.GUIAbstractSetup;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.AbstractSetup;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractMainViewmodel extends Viewmodel {

    public final static String userMustPlaceNewStonePropertyName = "userMustPlaceNewStone";
    // TODO : correct to declare here this variable?

    @Nullable
    private Match match;

    @Nullable
    private Game currentGame;

    @Nullable
    private Board currentBoard;

    public AbstractMainViewmodel() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Board.boardMatrixPropertyName -> {
                Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
                firePropertyChange(Board.boardMatrixPropertyName, null, cell);  // TODO : inappropriate property name (observed in GomokuCell)
            }
            case Game.gameEndedPropertyName -> endGame();
            case Game.currentPlayerPropertyName -> {
                Player currentPlayer = (Player) evt.getNewValue();
                placeStoneIfGameNotEndedAndIsCPUPlayingOrElseNotifyTheView(currentPlayer);
            }
        }
    }

    public void createMatchFromSetupAndStartGame(AbstractSetup abstractSetup) {
        setMatch(new Match(
                abstractSetup.getBoardSizeValue(), abstractSetup.getNumberOfGames(), abstractSetup.getPlayers()));
        startNewGame();
    }

    public GUIAbstractSetup createGUISetup(Pair<String, Boolean> player1Info, Pair<String, Boolean> player2Info,
                                           String numberOfGames, String boardSize) {

        var playerOne = player1Info.getValue()
                ? new CPUPlayer(player1Info.getKey())
                : new Player(player1Info.getKey());
        var playerTwo = player2Info.getValue()
                ? new CPUPlayer(player2Info.getKey())
                : new Player(player2Info.getKey());

        PositiveInteger n = new PositiveInteger(Integer.parseInt(numberOfGames));
        PositiveOddInteger s = BoardSizes.fromString(boardSize).getBoardSize();

        return new GUIAbstractSetup(playerOne,playerTwo, n, s);
    }

    public abstract void startNewMatch();

    public void startNewGame() {
        initializeNewGame();
        placeStoneIfGameNotEndedAndIsCPUPlayingOrElseNotifyTheView(getCurrentPlayer());
    }

    public void startExtraGame() {
        addAnExtraGameToThisMatch();
        startNewGame();
    }

    protected void addAnExtraGameToThisMatch() {
        Objects.requireNonNull(match).addAnExtraGame();
    }

    public synchronized boolean isMatchEnded() {
        return Objects.requireNonNull(match).isEnded();
    }

    protected synchronized boolean isCurrentGameEnded() {
        return Objects.requireNonNull(currentGame).isThisGameEnded();
    }

    protected void setMatch(@NotNull Match match) {
        this.match = Objects.requireNonNull(match);
    }

    @NotNull
    public String getCurrentBoardAsString() {
        return Objects.requireNonNull(currentBoard).toString();
    }

    protected void initializeNewGame() {
        try {
            currentGame = Objects.requireNonNull(match).startNewGame();
            currentBoard = currentGame.getBoard();
            observe(currentGame);
            observe(currentBoard);
            firePropertyChange(Game.newGameStartedPropertyName, false, true);
        } catch (Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        stopObserving(Objects.requireNonNull(currentGame));
        stopObserving(Objects.requireNonNull(currentBoard));
        firePropertyChange(Game.gameEndedPropertyName, false, true);
    }

    @NotNull
    protected Game getCurrentGame() {
        return Objects.requireNonNull(currentGame);
    }

    @NotNull
    protected Board getCurrentBoard() {
        return Objects.requireNonNull(currentBoard);
    }

    private void placeStone(@NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        Match.executeMoveOfPlayerInGame(getCurrentGame(), Objects.requireNonNull(coordinates));
    }

    public void placeStoneFromUser(@NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        if (!(getCurrentPlayer() instanceof CPUPlayer)) {
            placeStone(coordinates);
        }
    }

    protected void placeStoneIfGameNotEndedAndIsCPUPlayingOrElseNotifyTheView(Player currentPlayer) {
        if (!isCurrentGameEnded()) {
            if (currentPlayer instanceof CPUPlayer) {
                try {
                    placeStone(((CPUPlayer) currentPlayer)
                            .chooseRandomEmptyCoordinates(getCurrentBoard()));
                } catch (Board.NoMoreEmptyPositionAvailableException |
                        Board.PositionAlreadyOccupiedException e) {
                    e.printStackTrace();    // TODO : handle this
                }
            } else {
                firePropertyChange(userMustPlaceNewStonePropertyName, false, true); // TODO : where is the property?
            }
        }
    }

//    private void placeStoneWithDelayIfIsCpu(final long delay) {
// TODO : viewmodel should run on separate thread (not the same of gui)
//
// Here we want a Thread.sleep(200) so we can see how the game evolves when CPU vs CPU (otherwise it is instantaneous)
// but this would block the GUI. At the same time, scheduling this task with a time may break the logic
// The solution might be to put the entire method placeStone in another thread
//    }

    public int getBoardSize() {
        try {
            return Objects.requireNonNull(currentBoard).getSize();
        } catch (NullPointerException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .severe("The board is null but should not.\n\t" +
                            Arrays.stream(e.getStackTrace())
                                    .map(StackTraceElement::toString)
                                    .collect(Collectors.joining("\n\t")));
            throw e;
        }
    }

    public Map<Player, NonNegativeInteger> getScoreOfMatch() {
        return Objects.requireNonNull(match).getScore();
    }

    public Player getCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getCurrentPlayer();
    }

    public Player getCurrentBlackPlayer() {
        return Objects.requireNonNull(match).getCurrentBlackPlayer();
    }

    public Player getCurrentWhitePlayer() {
        return Objects.requireNonNull(match).getCurrentWhitePlayer();
    }

    @Nullable
    public Player getWinnerOfTheMatch() throws Match.MatchNotEndedException {
        return Objects.requireNonNull(match).getWinner();
    }

    @Nullable
    public Player getWinnerOfTheGame() throws Game.GameNotEndedException {
        return Objects.requireNonNull(currentGame).getWinner();
    }

    public ZonedDateTime getGameStartTime() {
        return Objects.requireNonNull(currentGame).getStart();
    }
}