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

    // TODO : correct to declare here this variable?
    public final static String userMustPlaceNewStonePropertyName = "userMustPlaceNewStone";

    public final static String currentPlayerPropertyName = Game.currentPlayerPropertyName;
    public final static String lastMoveCoordinatesPropertyName = Board.lastMoveCoordinatesPropertyName;

    @Nullable
    private Match match;

    public enum CurrentGameStatus {GAME_STARTED, USER_MUST_PLACE, USER_MUST_NOT_PLACE, GAME_ENDED}

    //    @NotNull
//    public final ObservableProperty<CurrentGameStatus> currentGameStatus = new ObservableProperty<>(this);//TODO : PUBLIC?
//
//    @NotNull
//    public final ObservableProperty<Game> currentGame = new ObservableProperty<>(this); // TODO:public???
//
//    @NotNull
//    private final ObservableProperty<Boolean> currentGameEnded = new ObservableProperty<>(this);
    @Nullable
    private Game currentGame;

    //    @NotNull
//    private final ObservableProperty<Player> currentPlayer = new ObservableProperty<>(this);
//
//    @NotNull
//    private final ObservableProperty<Board> currentBoard = new ObservableProperty<>(this);  // TODO : needed?
    @Nullable
    private Board currentBoard;

    public AbstractMainViewmodel() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        // TODO : refactor needed
//
//        final String evtName = evt.getPropertyName();
//     /*   if (Objects.equals(evtName, currentGame.getPropertyValue().gameEnded.getPropertyNameOrElseThrow())) {    // TODO : switch may be better
//            // TODO: message chain code smell (the property is the game itself)
////            if (isCurrentGameEnded()) {
////                endGame();
////            }
//            if ((boolean) evt.getNewValue()) {
//                endGame();
//            }  else */if (Objects.equals(evtName, currentGame.getPropertyValue().currentPlayer.getPropertyNameOrElseThrow())) {// TODO: message chain code smell (the property is the game itself)
//            currentPlayer.setPropertyValueAndFireIfPropertyChange((Player) evt.getNewValue());
//            if (!isCurrentGameEnded()) {
//                placeStoneIfCPUPlayingWithDelayOrElseNotifyTheView(currentPlayer.getPropertyValue(), 0);// TODO : delay?
//            }
//        } else if (Objects.equals(evtName, Board.boardMatrixPropertyName)) {    // TODO : property name to be changed into a property
//            firePropertyChange(Board.boardMatrixPropertyName, evt.getNewValue()); // TODO: use a Property
//        } else if (Objects.equals(evtName, Board.boardMatrixPropertyName)) {    // TODO : property name to be changed into a property
//            firePropertyChange(Board.boardMatrixPropertyName, evt.getNewValue()); // TODO: use a ObservableProperty
//        }
        switch (evt.getPropertyName()) {
            case Board.lastMoveCoordinatesPropertyName -> firePropertyChange(
                    lastMoveCoordinatesPropertyName, evt.getOldValue(), evt.getNewValue());
            case Game.isThisGameEndedPropertyName -> {
                if ((Boolean) evt.getNewValue()) {
                    endGame();
                }
            }
            case Game.currentPlayerPropertyName -> {
                Player currentPlayer = (Player) evt.getNewValue();
                Player oldValue = (Player) evt.getOldValue();
                firePropertyChange(currentPlayerPropertyName, oldValue, currentPlayer);
                if (!isCurrentGameEnded()) {
                    placeStoneIfCPUPlayingWithDelayOrElseNotifyTheView(currentPlayer, 0);
                }
            }
        }
    }

        public void triggerFirstMove () {
        if (currentGame == null) {
                throw new NullPointerException("Cannot invoke this method before starting the game (current game is null)");
            }
            placeStoneIfCPUPlayingWithDelayOrElseNotifyTheView(getCurrentPlayer(), 0);
        }

        public void createMatchFromSetupAndStartGame (Setup setup){
            setMatch(new Match(setup));
            startNewGame();
        }

        public abstract void startNewMatch ();

        public void startNewGame () {
            initializeNewGame();
        }

        public void startExtraGame () {
            addAnExtraGameToThisMatch();
            startNewGame();
        }

        protected void addAnExtraGameToThisMatch () {
            Objects.requireNonNull(match).addAnExtraGame();
        }

        public synchronized boolean isMatchEnded () {
            return Objects.requireNonNull(match).isEnded();
        }

        protected synchronized boolean isCurrentGameEnded () {
        return Objects.requireNonNull(currentGame).isThisGameEnded();
        }

        protected void setMatch (@NotNull Match match){
            this.match = Objects.requireNonNull(match);
        }

        @NotNull
        public String getCurrentBoardAsString () {
        return Objects.requireNonNull(currentBoard).toString();
        }

        protected void initializeNewGame () {
            try {
//            Game newGame = Objects.requireNonNull(match).startNewGame();
//            Board newBoard = newGame.getBoard();
//            observe(newGame);
//            observe(newBoard);
//            new PropertyObserver<>(newGame.gameEnded, evt -> {
//                if ((boolean) evt.getNewValue()) {
//                    endGame();
//                }
//            });
//            currentGame.setPropertyValueAndFireIfPropertyChange(newGame);   // TODO : property needed?
//            currentBoard.setPropertyValueAndFireIfPropertyChange(newBoard);   // TODO : property needed?
//            currentGameStatus.setPropertyValueAndFireIfPropertyChange(CurrentGameStatus.GAME_STARTED);
            currentGame = Objects.requireNonNull(match).startNewGame();
            currentBoard = currentGame.getBoard();
            observe(currentGame);
            observe(currentBoard);
            firePropertyChange(Game.newGameStartedPropertyName, false, true);
            } catch (Match.MatchEndedException e) {
                e.printStackTrace();
            }
        }

        public void endGame () {
        stopObserving(Objects.requireNonNull(currentGame));
        stopObserving(Objects.requireNonNull(currentBoard));
        firePropertyChange(Game.isThisGameEndedPropertyName, false, true);
        }

        @NotNull
        protected Game getCurrentGame () {
        return Objects.requireNonNull(currentGame);
        }

        @NotNull
        protected Board getCurrentBoard () {
        return Objects.requireNonNull(currentBoard);
        }

        private void placeStone ( @NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
            Match.executeMoveOfPlayerInGame(getCurrentGame(), Objects.requireNonNull(coordinates));
        }

        public void placeStoneFromUser ( @NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
            if (!(getCurrentPlayer() instanceof CPUPlayer)) {
                //TODO: why two methods that do the same control?
                placeStone(coordinates);
            }
        }

        private void placeStoneIfCPUPlayingWithDelayOrElseNotifyTheView (Player currentPlayer,int delayOfCpuMove){
            //TODO: why currentPlayer and not getCurrentPlayer?
            runOnSeparateThread(() -> {
                if (currentPlayer instanceof CPUPlayer cpuPlayer) {
                    try {
                        Thread.sleep(delayOfCpuMove);
                        placeStone(cpuPlayer.chooseSmartEmptyCoordinates(getCurrentBoard()));
                } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | InterruptedException e) {
                        e.printStackTrace();    // TODO : handle this
                    }
                } else {
                firePropertyChange(userMustPlaceNewStonePropertyName, false, true); // TODO : where is the property?
                }
            });
        }

        public void forceReFireAllCells () {
        // TODO: Rethink this
//        firePropertyChange(Board.lastMoveCoordinatesPropertyName, null);
        }

        public int getBoardSize () {
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

        public Map<Player, NonNegativeInteger> getScoreOfMatch () {
            return Objects.requireNonNull(match).getScore();
        }

        public Player getCurrentPlayer () {
        return Objects.requireNonNull(currentGame).getCurrentPlayer();
        }

    public Stone.Color getColorOfCurrentPlayer() {
        return Objects.requireNonNull(currentGame).getColorOfPlayer(getCurrentPlayer());
        }

        public Player getCurrentBlackPlayer () {
            return Objects.requireNonNull(match).getCurrentBlackPlayer();
        }

        public Player getCurrentWhitePlayer () {
            return Objects.requireNonNull(match).getCurrentWhitePlayer();
        }

    public Cell getCellAtCoordinatesInCurrentBoard(Coordinates coordinates) {
        return Objects.requireNonNull(currentBoard).getCellAtCoordinates(coordinates);
        }

        @Nullable
        public Player getWinnerOfTheMatch () throws Match.MatchNotEndedException {
            return Objects.requireNonNull(match).getWinner();
        }

        @Nullable
        public Player getWinnerOfTheGame () throws Game.GameNotEndedException {
        return Objects.requireNonNull(currentGame).getWinner();
        }

        public ZonedDateTime getGameStartTime () {
        return Objects.requireNonNull(currentGame).getStart();
        }
    }