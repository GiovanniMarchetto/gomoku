package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractMainViewmodel extends Viewmodel {

    private Match match;

    private Game currentGame;

    private Board currentBoard;

    private boolean userCanPlace = true;

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

    //    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        switch (evt.getPropertyName()) {
//            case Board.BoardMatrixPropertyName -> {
//                Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
//                firePropertyChange(Board.BoardMatrixPropertyName, null, cell);
//            }
//            case Setup.setupCompletedPropertyName -> {
//                Setup setup = (Setup) evt.getNewValue();
//                match = new Match(setup.getBoardSizeValue(), setup.getNumberOfGames(), setup.getPlayers());
//                startNewGameAndPassToMainView();
//            }
//            case Game.gameEndedPropertyName -> {
//                endGame();
//                SceneController.passToNewScene(SceneController.ViewName.SUMMARY_VIEW);
//            }
//            case SummaryView.continueAfterSummaryPropertyName -> {
//                startNewGameAndPassToMainView();
//            }
//            case SummaryView.newMatchAfterSummaryPropertyName -> {
//                SceneController.passToNewScene(SceneController.ViewName.START_VIEW);
//            }
//            case SummaryView.extraGameAfterSummaryPropertyName -> {
//                match.addAnExtraGame();
//                startNewGameAndPassToMainView();
//            }
//            default -> throw new IllegalArgumentException("Property name " + evt.getPropertyName() + " not found!");
//        }
//    }

    public void startNewGame() {
        try {
            currentGame = match.startNewGame();
            currentBoard = currentGame.getBoard();
            observe(currentGame);
            observe(currentBoard);
        } catch (Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        // *Avengers theme plays*
        stopObserving(currentGame);
        stopObserving(currentBoard);
    }

    public void placeStone(@Nullable final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        Player currentPlayer = currentGame.getCurrentPlayer();
        if (currentPlayer instanceof CPUPlayer || userCanPlace) {
            Match.executeMoveOfPlayerInGame(currentGame,
                    currentPlayer instanceof CPUPlayer ?
                            ((CPUPlayer) currentPlayer).chooseRandomEmptyCoordinates(currentBoard) : coordinates);
            placeStoneWithDelayIfIsCpu(200);
        }
    }


    private void placeStoneWithDelayIfIsCpu(final long delay) {
        if (!currentGame.isThisGameEnded() && currentGame.getCurrentPlayer() instanceof CPUPlayer) {
            userCanPlace = false;

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        placeStone(null);
                    } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                        e.printStackTrace();        // TODO : rethink about this method
                    }
                    userCanPlace = true;
                    timer.cancel();
                }
            };
            timer.schedule(task, delay);
        }
    }

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
    public Player getWinnerOfTheMatch() throws Match.MatchEndedException {
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