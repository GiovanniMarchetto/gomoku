package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.ui.support.Setup;

import java.beans.PropertyChangeEvent;

public class MainViewmodel extends Viewmodel {

    private Match match;

    private Game currentGame;

    private Board currentBoard;

    private Player currentPlayer;

    public MainViewmodel() {

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
        return currentBoard.getSize();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Board.BoardMatrixPropertyName)) {
            // Propagate event, note that the (Game) Board changes during the Match
            Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
            firePropertyChange(Board.BoardMatrixPropertyName, null, cell);
        } else if (evt.getPropertyName().equals(Setup.setupCompletedPropertyName)) {
            var setup = (Setup) evt.getNewValue();
            currentPlayer = setup.getPlayers()[0];
            match = new Match(setup.getBoardSizeValue(), setup.getNumberOfGames(), setup.getPlayers());
            startNewGame();
        }
    }
}
