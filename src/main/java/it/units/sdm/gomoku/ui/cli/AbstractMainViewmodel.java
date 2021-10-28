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

    public final static String userCanPlacePropertyName = "userCanPlace";

    private Match match;

    private Game currentGame;

    private Board currentBoard;

    private boolean userCanPlace = false;

    public AbstractMainViewmodel() {
    }

    private void setUserCanPlace(boolean userCanPlace) {
        boolean oldValue = this.userCanPlace;
        if (oldValue != userCanPlace) {
            this.userCanPlace = userCanPlace;
            firePropertyChange(userCanPlacePropertyName, oldValue, userCanPlace);
        }
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
            firePropertyChange(Game.newGameStartedPropertyName, false, true);
            placeStoneWithDelayIfIsCpu(500);    // TODO : magicnumber?
        } catch (Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        setUserCanPlace(false); // TODO : rethink about this
        stopObserving(currentGame);
        stopObserving(currentBoard);
        firePropertyChange(Game.gameEndedPropertyName, false, true);
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
        if (!currentGame.isThisGameEnded()) {
            if (currentGame.getCurrentPlayer() instanceof CPUPlayer) {
                setUserCanPlace(false);

//            Timer timer = new Timer();
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
                try {
                    placeStone(null);
                } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                    e.printStackTrace();        // TODO : rethink about this method
                }
                setUserCanPlace(true);
//                    timer.cancel();
//                }
//            };
//            timer.schedule(task, delay);

                // TODO : viewmodel should run on separate thread (not the same of gui)
                //
                // Here we want a Thread.sleep(200) so we can see how the game evolves when CPU vs CPU (otherwise it is instantaneous)
                // but this would block the GUI. At the same time, scheduling this task with a time may break the logic
                // The solution might be to put the entire method placeStone in another thread

            } else {
                setUserCanPlace(true);
            }
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