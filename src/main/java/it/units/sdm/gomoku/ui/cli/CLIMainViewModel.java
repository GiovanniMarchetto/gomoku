package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.support.Setup;

import java.beans.PropertyChangeEvent;
import java.io.IOException;

import static it.units.sdm.gomoku.ui.cli.CLIApplication.newMatchAfterSummaryPropertyName;

public class CLIMainViewModel extends AbstractMainViewmodel {

    // TODO : rethink about this when discussing about deleting commander button

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Setup.setupCompletedPropertyName -> {
                Setup setup = (Setup) evt.getNewValue();
                setMatch(new Match(setup.getBoardSizeValue(),
                        setup.getNumberOfGames(), setup.getPlayers()));
                startNewGame();
            }
            case Board.BoardMatrixPropertyName -> { // TODO : needed in CLI ?
                Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
//                firePropertyChange(Board.BoardMatrixPropertyName, null, cell);
                // TODO : inappropriate property name (observed in GomokuCell)
            }
            case Game.gameEndedPropertyName -> endGame();
            case CLIApplication.continueAfterSummaryPropertyName -> startNewGame();
            case CLIApplication.extraGameAfterSummaryPropertyName -> {
                if ((boolean) evt.getNewValue()) {
                    addAnExtraGameToThisMatch();
                    startNewGame();
                }
            }
            case newMatchAfterSummaryPropertyName -> {
                if ((boolean) evt.getNewValue()) {
                    try {
                        new CLIMainView(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            default -> throw new IllegalArgumentException("Property name " + evt.getPropertyName() + " not found!");
        }
    }

}
