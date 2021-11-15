package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MainViewmodel extends Viewmodel {

    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Player> currentPlayerProperty;
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Game.Status> currentGameStatusProperty;
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> userMustPlaceNewStoneProperty;
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Coordinates> lastMoveCoordinatesProperty;
    @NotNull
    private final List<PropertyObserver<?>> modelPropertyObservers;
    @Nullable
    private Match match;
    @Nullable
    private Game currentGame;
    @Nullable
    private Board currentBoard;

    public MainViewmodel() {
        this.currentPlayerProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.currentGameStatusProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.userMustPlaceNewStoneProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.lastMoveCoordinatesProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.modelPropertyObservers = new ArrayList<>();
    }

    protected void initializeNewGame() {//TODO: need? for me inline
        try {
            currentGame = Objects.requireNonNull(match).startNewGame();
            currentBoard = currentGame.getBoard();
            observePropertiesOfModel();

            observe(currentGame);   // TODO : should fade away
            observe(currentBoard);

        } catch (Match.MatchEndedException | Match.MaxNumberOfGamesException e) {
            e.printStackTrace();    // TODO : handle this exception
        }
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

        modelPropertyObservers.clear();

        assert currentGame != null;
        addObservedProperty(
                currentGame.getCurrentPlayer(),
                evt -> {
                    currentPlayerProperty.setPropertyValueAndFireIfPropertyChange((Player) evt.getNewValue());
                    if (!isCurrentGameEnded()) {
                        Objects.requireNonNull(currentPlayerProperty.getPropertyValue()).makeMove(getCurrentGame());
                    }
                });

        addObservedProperty(
                currentGame.getGameStatus(),
                evt -> {
                    currentGameStatusProperty.setPropertyValueAndFireIfPropertyChange((Game.Status) evt.getNewValue());
                    if (evt.getNewValue().equals(Game.Status.ENDED)) {
                        endGame();
                    }
                });

        assert currentBoard != null;
        addObservedProperty(
                currentBoard.getLastMoveCoordinatesProperty(),
                evt -> lastMoveCoordinatesProperty.setPropertyValueAndFireIfPropertyChange(
                        (Coordinates) evt.getNewValue()));

        Stream.of(getCurrentBlackPlayer(), getCurrentWhitePlayer())
                .forEach(player ->
                        addObservedProperty(
                                player.getCoordinatesRequiredToContinueProperty(),
                                evt -> userMustPlaceNewStoneProperty.setPropertyValueAndFireIfPropertyChange((boolean) evt.getNewValue())));

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

    public synchronized boolean isMatchEndedWithADraw() throws Match.MatchNotEndedException {
        return Objects.requireNonNull(match).isADraw();
    }


    protected synchronized boolean isCurrentGameEnded() {
        return Objects.requireNonNull(currentGame).isEnded();
    }

    protected void setMatch(@NotNull Match match) {
        this.match = Objects.requireNonNull(match);
    }

    public abstract void endGame();

    public void forceReFireAllCells() {
        // TODO: Rethink this
        //TODO: to test if implemented
    }

    public void placeStoneFromUser(@NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException, Game.GameEndedException, Board.CellOutOfBoardException {
        if (Boolean.TRUE.equals(userMustPlaceNewStoneProperty.getPropertyValue())) {
            Objects.requireNonNull(getCurrentPlayer())
                    .setNextMove(Objects.requireNonNull(coordinates), Objects.requireNonNull(currentGame));
        }
    }

    @NotNull
    public String getCurrentBoardAsString() {
        return getCurrentBoard().toString();
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
        return Objects.requireNonNull(currentGame).getCurrentPlayer().getPropertyValue();
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
    public Cell getCellAtCoordinatesInCurrentBoard(@NotNull final Coordinates coordinates)
            throws Board.CellOutOfBoardException {
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