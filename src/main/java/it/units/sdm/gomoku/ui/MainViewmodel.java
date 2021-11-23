package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MainViewmodel extends Viewmodel {

    @NotNull
    private ObservablePropertySettable<Player> currentPlayerProperty;
    @NotNull
    private ObservablePropertySettable<Game.Status> currentGameStatusProperty;
    @NotNull
    private ObservablePropertySettable<Boolean> userMustPlaceNewStoneProperty;
    @NotNull
    private ObservablePropertySettable<Coordinates> lastMoveCoordinatesProperty;
    @NotNull
    private final List<PropertyObserver<?>> modelPropertyObservers;
    @Nullable
    private Match match;
    @Nullable
    private Game currentGame;
    @Nullable
    private Board currentBoard;

    public MainViewmodel() {
        this.currentPlayerProperty = new ObservablePropertySettable<>();
        this.currentGameStatusProperty = new ObservablePropertySettable<>();
        this.userMustPlaceNewStoneProperty = new ObservablePropertySettable<>();
        this.lastMoveCoordinatesProperty = new ObservablePropertySettable<>();
        this.modelPropertyObservers = new ArrayList<>();
    }

    private <ObservedPropertyValueType> void addObservedProperty(
            @NotNull final ObservableProperty<ObservedPropertyValueType> observableProperty,
            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        // TODO : test and improve logic: e.g. the same property cannot be observed more than once
        Objects.requireNonNull(modelPropertyObservers)
                .add(new PropertyObserver<>(
                        Objects.requireNonNull(observableProperty), Objects.requireNonNull(actionOnPropertyChange)));
    }

    private void observePropertiesOfModel() {

        modelPropertyObservers.forEach(PropertyObserver::stopObserving); //TODO: this should stay here? Refactor!

        modelPropertyObservers.clear();

        assert currentGame != null;
        addObservedProperty(
                currentGame.getCurrentPlayerProperty(),
                evt -> {
                    currentPlayerProperty.setPropertyValue((Player) evt.getNewValue());
                    if (!isCurrentGameEnded()) {
                        new Thread(() -> {
                            try {
                                Objects.requireNonNull(currentPlayerProperty.getPropertyValue()).makeMove();
                            } catch (GameEndedException e) {
                                Utility.getLoggerOfClass(getClass())
                                        .log(Level.SEVERE, "Error with current player property: " + e.getMessage(), e);
                                throw new IllegalStateException(e);
                            } catch (CellOutOfBoardException | CellAlreadyOccupiedException e) {
                                //TODO: notify the user and re-request coordinates
                            }
                        }).start();
                    }
                });

        addObservedProperty(
                currentGame.getGameStatusProperty(),
                evt -> {
                    currentGameStatusProperty.setPropertyValue((Game.Status) evt.getNewValue());
                    if (evt.getNewValue().equals(Game.Status.ENDED)) {
                        endGame();
                    }
                });

        assert currentBoard != null;
        addObservedProperty(
                currentBoard.getLastMoveCoordinatesProperty(),
                evt -> lastMoveCoordinatesProperty.setPropertyValue(
                        (Coordinates) evt.getNewValue()));

        Stream.of(getCurrentBlackPlayer(), getCurrentWhitePlayer())
                .forEach(player ->
                        addObservedProperty(
                                player.getCoordinatesRequiredToContinueProperty(),
                                evt -> userMustPlaceNewStoneProperty.setPropertyValue((boolean) evt.getNewValue())));

    }

    public void triggerFirstMove() {
        if (currentGame == null) {
            throw new NullPointerException("Cannot invoke this method before starting the game (current game is null)");
        }
        try {
            currentGame.start();
        } catch (GameAlreadyStartedException e) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "Cannot invoke this method if a game is already started", e);
            throw new IllegalStateException(e);
        }
    }

    public void createMatchFromSetupAndInitializeNewGame(Setup setup) {
        setMatch(new Match(setup));
        // TODO: code duplication (ctor)
        this.currentPlayerProperty = new ObservablePropertySettable<>();
        this.currentGameStatusProperty = new ObservablePropertySettable<>();
        this.userMustPlaceNewStoneProperty = new ObservablePropertySettable<>();
        this.lastMoveCoordinatesProperty = new ObservablePropertySettable<>();
        initializeNewGame();
    }

    public abstract void startNewMatch();

    public void initializeNewGame() {
        try {
            currentGame = Objects.requireNonNull(match).initializeNewGame();
            currentBoard = currentGame.getBoard();
            observePropertiesOfModel();

        } catch (MatchEndedException e) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "Cannot invoke this method if the match is ended", e);
            throw new IllegalStateException(e);
        } catch (GameNotEndedException e) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "Cannot invoke this method if the previous game is not ended", e);
            throw new IllegalStateException(e);
        }
    }

    public void startExtraGame() {
        addAnExtraGameToThisMatch();
        initializeNewGame();
    }

    protected void addAnExtraGameToThisMatch() {
        Objects.requireNonNull(match).incrementTotalNumberOfGames();
    }

    public synchronized boolean isMatchEnded() {
        return Objects.requireNonNull(match).isEnded();
    }

    public synchronized boolean isMatchEndedWithADraw() throws MatchNotEndedException {
        return Objects.requireNonNull(match).isADraw();
    }


    protected synchronized boolean isCurrentGameEnded() {
        return Objects.requireNonNull(currentGame).isEnded();
    }

    protected void setMatch(@NotNull Match match) {
        this.match = Objects.requireNonNull(match);
    }

    public abstract void endGame();

    public void placeStoneFromUser(@NotNull final Coordinates coordinates)
            throws CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException {
        if (Boolean.TRUE.equals(userMustPlaceNewStoneProperty.getPropertyValue())) {
            Objects.requireNonNull(getCurrentPlayer())
                    .setMoveToBeMade(Objects.requireNonNull(coordinates));
        }
    }

    @NotNull
    public String getCurrentBoardAsString() {
        return getCurrentBoard().toString();
    }

    @NotNull
    public Game getCurrentGame() {
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
            //TODO: only for this the logger?
            Logger.getLogger(getClass().getCanonicalName())
                    .severe("The board is null but should not.\n\t" +
                            Arrays.stream(e.getStackTrace())
                                    .map(StackTraceElement::toString)
                                    .collect(Collectors.joining("\n\t")));
            throw e;
        }
    }

    @NotNull
    public Map<Player, NonNegativeInteger> getScoreOfMatch() {
        return Objects.requireNonNull(match).getScore();
    }

    @Nullable
    public Player getCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getCurrentPlayerProperty().getPropertyValue();
    }

    @NotNull
    public ObservableProperty<Player> getCurrentPlayerProperty() {
        return new ObservablePropertyProxy<>(currentPlayerProperty);
    }

    @NotNull
    public ObservableProperty<Game.Status> getCurrentGameStatusProperty() {
        return new ObservablePropertyProxy<>(currentGameStatusProperty);
    }

    @NotNull
    public ObservableProperty<Boolean> getUserMustPlaceNewStoneProperty() {
        return new ObservablePropertyProxy<>(userMustPlaceNewStoneProperty);
    }

    @NotNull
    public ObservableProperty<Coordinates> getLastMoveCoordinatesProperty() {
        return new ObservablePropertyProxy<>(lastMoveCoordinatesProperty);
    }

    @NotNull
    public Color getColorOfCurrentPlayer() {
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
    public Cell getCellAtCoordinatesInCurrentBoard(@NotNull final Coordinates coordinates)
            throws CellOutOfBoardException {
        return Objects.requireNonNull(currentBoard).getCellAtCoordinates(coordinates);
    }

    @Nullable
    public Player getWinnerOfTheMatch() throws MatchNotEndedException {
        return Objects.requireNonNull(match).getWinner();
    }

    @Nullable
    public Player getWinnerOfTheGame() throws GameNotEndedException {
        return Objects.requireNonNull(currentGame).getWinner();
    }

    @NotNull
    public ZonedDateTime getGameStartTime() {
        return Objects.requireNonNull(currentGame).getCreationTime();
    }
}