package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
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

public abstract class AbstractMainViewmodel extends Viewmodel {

    @NotNull
    private final ObservableProperty<Player> currentPlayerProperty;
    @NotNull
    private final ObservableProperty<Game.Status> currentGameStatusProperty;
    @NotNull
    private final ObservableProperty<Boolean> userMustPlaceNewStoneProperty;
    @NotNull
    private final ObservableProperty<Coordinates> lastMoveCoordinatesProperty;
    @Nullable
    private Match match;
    //
//    @NotNull
//    public final ObservableProperty<Game> currentGame = new ObservableProperty<>(this); // TODO:public???
//
    @Nullable
    private Game currentGame;
    //
//    @NotNull
//    private final ObservableProperty<Board> currentBoard = new ObservableProperty<>(this);  // TODO : needed?
    @Nullable
    private Board currentBoard;

    public AbstractMainViewmodel() {
        this.currentPlayerProperty = new ObservableProperty<>();
        this.currentGameStatusProperty = new ObservableProperty<>();
        this.userMustPlaceNewStoneProperty = new ObservableProperty<>();
        this.lastMoveCoordinatesProperty = new ObservableProperty<>();
    }

    protected void initializeNewGame() {
        try {
            currentGame = Objects.requireNonNull(match).startNewGame();
            currentBoard = currentGame.getBoard();
            new PropertyObserver<>(
                    currentGame.getCurrentPlayer(),
                    evt -> {
                        currentPlayerProperty.setPropertyValueAndFireIfPropertyChange((Player) evt.getNewValue());
                        if (!isCurrentGameEnded()) {
                            runOnSeparateThread(() -> {
                                try {
                                    Objects.requireNonNull(currentPlayerProperty.getPropertyValue()).makeMove(getCurrentGame());
                                } catch (Board.BoardIsFullException e) {
                                    e.printStackTrace(); // TODO: handle this: this should never happen (I think?)
                                }
                            });
                        }
                    });
            new PropertyObserver<>(
                    currentGame.getGameStatus(),
                    evt -> {
                        currentGameStatusProperty.setPropertyValueAndFireIfPropertyChange((Game.Status) evt.getNewValue());
                        if (evt.getNewValue().equals(Game.Status.ENDED)) {
                            endGame();
                        }
                    });
            new PropertyObserver<>(currentBoard.getLastMoveCoordinatesProperty(),
                    evt -> lastMoveCoordinatesProperty.setPropertyValueAndFireIfPropertyChange((Coordinates) evt.getNewValue()));
            Arrays.asList(getCurrentBlackPlayer(), getCurrentWhitePlayer())
                    .forEach(player -> new PropertyObserver<>(player.getCoordinatesRequiredToContinueProperty(),
                            evt -> userMustPlaceNewStoneProperty.setPropertyValueAndFireIfPropertyChange((boolean) evt.getNewValue())));

            observe(currentGame);   // TODO : should fade away
            observe(currentBoard);

        } catch (Match.MatchEndedException | Match.MaxNumberOfGamesException e) {
            e.printStackTrace();    // TODO : handle this exception
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    public void triggerFirstMove() {
        if (currentGame == null) {
            throw new NullPointerException("Cannot invoke this method before starting the game (current game is null)");
        }
        currentGame.start();
    }

    public void createMatchFromSetupAndStartGame(Setup setup) {
        setMatch(new Match(setup));
        observe(getCurrentBlackPlayer());   // TODO : comment observe(..) method usages
        observe(getCurrentWhitePlayer());
        startNewGame();
    }

    public abstract void startNewMatch();

    public void startNewGame() {
        initializeNewGame();
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

    public synchronized boolean isMatchEndedWithADraft() throws Match.MatchNotEndedException {
        if (isMatchEnded()) {
            return Objects.requireNonNull(match).isADraft();
        }else {
            throw new Match.MatchNotEndedException();
        }
    }


    protected synchronized boolean isCurrentGameEnded() {
        return Objects.requireNonNull(currentGame).isEnded();
    }

    protected void setMatch(@NotNull Match match) {
        this.match = Objects.requireNonNull(match);
    }

    public void endGame() {
        stopObserving(Objects.requireNonNull(currentGame));
        stopObserving(Objects.requireNonNull(currentBoard));
    }

    public void forceReFireAllCells() {
        // TODO: Rethink this
    }

    public void placeStoneFromUser(@NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
        if (Boolean.TRUE.equals(userMustPlaceNewStoneProperty.getPropertyValue())) {
            HumanPlayer currentHumanPlayer = (HumanPlayer) Objects.requireNonNull(getCurrentPlayer());
            currentHumanPlayer.placeStone(coordinates);
        }
    }

    @NotNull
    public String getCurrentBoardAsString() {
        return Objects.requireNonNull(currentBoard).toString();
    }

    @NotNull
    protected Game getCurrentGame() {
        return Objects.requireNonNull(currentGame);
    }

    @NotNull
    protected Board getCurrentBoard() {
        return Objects.requireNonNull(currentBoard);
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
        return Objects.requireNonNull(match).getScore();
    }

    @Nullable
    public Player getCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getCurrentPlayer().getPropertyValue();
    }

    @NotNull
    public ObservableProperty<Player> getCurrentPlayerProperty() {
        return currentPlayerProperty;   // TODO: getting the property would allow to set the property and fire property change events (only the owner of the property should be able to do that, here we could simply a return a function "runnable" to make the binding without allowing to access the object fields
    }

    @NotNull
    public ObservableProperty<Game.Status> getCurrentGameStatusProperty() {
        return currentGameStatusProperty;
    }

    @NotNull
    public ObservableProperty<Boolean> getUserMustPlaceNewStoneProperty() {
        return this.userMustPlaceNewStoneProperty;
    }

    @NotNull
    public ObservableProperty<Coordinates> getLastMoveCoordinatesProperty() {
        return this.lastMoveCoordinatesProperty;
    }

    @NotNull
    public Stone.Color getColorOfCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getColorOfPlayer(Objects.requireNonNull(getCurrentPlayer()));
    }

    @NotNull
    public Player getCurrentBlackPlayer() {
        return Objects.requireNonNull(match).getCurrentBlackPlayer();
    }

    @NotNull
    public Player getCurrentWhitePlayer() {
        return Objects.requireNonNull(match).getCurrentWhitePlayer();
    }

    @NotNull
    public Cell getCellAtCoordinatesInCurrentBoard(Coordinates coordinates) {
        return Objects.requireNonNull(currentBoard).getCellAtCoordinates(coordinates);
    }

    @Nullable
    public Player getWinnerOfTheMatch() throws Match.MatchNotEndedException {
        return Objects.requireNonNull(match).getWinner();
    }

    @Nullable
    public Player getWinnerOfTheGame() throws Game.GameNotEndedException {
        return Objects.requireNonNull(currentGame).getWinner();
    }

    @NotNull
    public ZonedDateTime getGameStartTime() {
        return Objects.requireNonNull(currentGame).getStart();
    }
}