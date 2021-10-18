package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;

import java.beans.PropertyChangeEvent;

public class MainViewmodel extends Viewmodel {

    private final Match match;

    private Game currentGame;

    private Board currentBoard;

    private int boardSize = 19;

    private Player currentPlayer;

    public MainViewmodel() {
        Player p1 = new Player("Mario");
        Player p2 = new Player("Luigi");
        currentPlayer = p1;
        match = new Match(p1, p2, boardSize, 3);
        startNewGame();
    }

    public void startNewGame() {
        currentGame = match.startNewGame();
        currentBoard = currentGame.getBoard();
        observe(currentBoard);
    }

    public void endGame() {
        // *Avengers theme plays*
        stopObserving(currentBoard);
    }

    private void changeTurn() {
        currentPlayer = currentPlayer == match.getCurrentBlackPlayer() ? match.getCurrentWhitePlayer() : match.getCurrentBlackPlayer();
    }

    public void placeStone(Coordinates coordinates) {
        // TODO: re-do this
        try {
            Match.executeMoveOfPlayerInGame(currentPlayer, currentGame, coordinates);
            changeTurn();
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            e.printStackTrace();
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Board.BoardMatrixPropertyName)) {
            // Propagate event, note that the (Game) Board changes during the Match
            Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
            firePropertyChange(Board.BoardMatrixPropertyName, null, cell);
        }
    }
}
