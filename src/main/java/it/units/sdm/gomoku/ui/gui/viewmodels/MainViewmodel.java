package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.support.Setup;

import java.beans.PropertyChangeEvent;

public class MainViewmodel extends Viewmodel {

    private final Match match;

    private Game currentGame;

    private Board currentBoard;

    private Board.Stone[][] currentMatrix;

    private int boardSize = 19;

    private Board.Stone currStone = Board.Stone.WHITE;

    public MainViewmodel() {
        Player p1 = new Player("Mario");
        Player p2 = new Player("Luigi");
        match = new Match(p1, p2, boardSize, 3);
        startNewGame();
    }

    public void startNewGame() {
        currentGame = match.startNewGame();
        currentBoard = currentGame.getBoard();
        currentMatrix = currentBoard.getBoardMatrix();
        observe(currentBoard);
    }

    public void endGame() {
        // *Avengers theme plays*
        stopObserving(currentBoard);
    }

    public void placeStone(Coordinates coordinates) {
        // TODO: re-do this
        currStone = currStone == Board.Stone.BLACK ? Board.Stone.WHITE : Board.Stone.BLACK;
        try {
            currentBoard.occupyPosition(currStone, coordinates);
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
        if (evt.getPropertyName().equals(Setup.setupCompletedPropertyName)) {
            SceneController.passToNextScene();
        }
    }
}
