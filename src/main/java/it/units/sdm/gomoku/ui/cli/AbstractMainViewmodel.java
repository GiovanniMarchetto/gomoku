package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractMainViewmodel extends Viewmodel {

    public final static String userMustPlaceNewStonePropertyName = "userMustPlaceNewStone";

    private Match match;

    private Game currentGame;

    private Board currentBoard;

    public AbstractMainViewmodel() {
    }

    public void addAnExtraGameToThisMatch() {
        match.addAnExtraGame();
    }

    public synchronized boolean isMatchEnded() {
        return match.isEnded();
    }

    public synchronized boolean isCurrentGameEnded() {
        return currentGame.isThisGameEnded();
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @NotNull
    protected String getCurrentBoardAsString() {
        return currentBoard.toString();
    }

    public void initializeNewGame() {
        try {
            currentGame = match.startNewGame();
            currentBoard = currentGame.getBoard();
            observe(currentGame);
            observe(currentBoard);
            firePropertyChange(Game.newGameStartedPropertyName, false, true);
        } catch (Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        stopObserving(currentGame);
        stopObserving(currentBoard);
        firePropertyChange(Game.gameEndedPropertyName, false, true);
    }

    @NotNull
    protected Game getCurrentGame() {
        return currentGame;
    }

    @NotNull
    protected Board getCurrentBoard() {
        return currentBoard;
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


    protected void startNewGame() {
        initializeNewGame();
        placeStoneIfGameNotEndedAndIsCPUPlayingOrElseNotifyTheView(getCurrentPlayer());
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
        return match.getScore();
    }

    public Player getCurrentPlayer() {
        return currentGame.getCurrentPlayer();
    }

    public Player getCurrentBlackPlayer() {
        return match.getCurrentBlackPlayer();
    }

    public Player getCurrentWhitePlayer() {
        return match.getCurrentWhitePlayer();
    }

    @Nullable
    public Player getWinnerOfTheMatch() throws Match.MatchNotEndedException {
        return match.getWinner();
    }

    @Nullable
    public Player getWinnerOfTheGame() throws Game.GameNotEndedException {
        return currentGame.getWinner();
    }

    public ZonedDateTime getGameStartTime() {
        return currentGame.getStart();
    }
}