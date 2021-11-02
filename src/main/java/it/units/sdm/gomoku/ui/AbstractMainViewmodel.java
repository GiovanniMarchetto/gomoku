package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractMainViewmodel extends Viewmodel {

    // TODO : correct to declare here this variable?
    public final static String userMustPlaceNewStonePropertyName = "userMustPlaceNewStone";

    public final static String currentPlayerPropertyName = Game.currentPlayerPropertyName;

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
                firePropertyChange(Board.boardMatrixPropertyName, cell);
            }
            case Game.isThisGameEndedPropertyName -> {
                if ((Boolean) evt.getNewValue()) {
                    endGame();
                }
            }
            case Game.currentPlayerPropertyName -> {
                Player currentPlayer = (Player) evt.getNewValue();
                Player oldValue = (Player) evt.getOldValue();
                firePropertyChange(currentPlayerPropertyName, oldValue, currentPlayer);
                placeStoneIfGameNotEndedAndIsCPUPlayingOrElseNotifyTheView(currentPlayer);
            }
        }
    }

    public void createMatchFromSetupAndStartGame(Setup setup) {
        setMatch(new Match(setup));
        startNewGame();
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
        firePropertyChange(Game.isThisGameEndedPropertyName, false, true);
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
            if (currentPlayer instanceof CPUPlayer cpuPlayer) {
                try {
                    placeStone(cpuPlayer.chooseRandomEmptyCoordinates(getCurrentBoard()));
                } catch (Board.NoMoreEmptyPositionAvailableException |
                        Board.PositionAlreadyOccupiedException e) {
                    e.printStackTrace();    // TODO : handle this
                }
            } else {
                firePropertyChange(userMustPlaceNewStonePropertyName, false, true); // TODO : where is the property?
            }
        }
    }

    public void forceRefireAllCells() {
        IntStream.range(0, getBoardSize())
                .unordered().parallel()
                .boxed()
                .flatMap(i -> IntStream.range(0, getBoardSize())
                        .unordered().parallel()
                        .mapToObj(j -> new Coordinates(i, j)))
                .map(c -> new Board.ChangedCell(c, getStoneAtCoordinatesInCurrentBoard(c), currentBoard))
                .forEach(c -> firePropertyChange(Board.boardMatrixPropertyName, c));
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

    public Board.Stone getStoneOfCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getStoneOfPlayer(getCurrentPlayer());
    }

    public Player getCurrentBlackPlayer() {
        return Objects.requireNonNull(match).getCurrentBlackPlayer();
    }

    public Player getCurrentWhitePlayer() {
        return Objects.requireNonNull(match).getCurrentWhitePlayer();
    }

    public Board.Stone getStoneAtCoordinatesInCurrentBoard(Coordinates coordinates) {
        return Objects.requireNonNull(currentBoard).getStoneAtCoordinates(coordinates);
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