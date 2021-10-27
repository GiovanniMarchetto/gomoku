package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.support.Setup;

import java.beans.PropertyChangeEvent;
import java.util.Scanner;

public class CLIMainViewModel extends AbstractMainViewmodel {

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
//                firePropertyChange(Board.BoardMatrixPropertyName, null, cell);  // TODO : inappropriate property name (observed in GomokuCell)
            }
            case Game.gameEndedPropertyName -> endGame();
            case CLIApplication.continueAfterSummaryPropertyName -> startNewGame();
            case CLIApplication.extraGameAfterSummaryPropertyName -> {
                addAnExtraGameToThisMatch();
                startNewGame();
            }
            default -> throw new IllegalArgumentException("Property name " + evt.getPropertyName() + " not found!");
        }
    }

    public void waitForAValidMoveOfAPlayerAndPlaceStone() {        // TODO : not tested and may need refactor (long method)
        if (getCurrentPlayer() instanceof CPUPlayer) {
            try {
                placeStone(null);   // TODO : is this the best way to handle CPUPlayer?
            } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                e.printStackTrace();
            }
        } else {
            int rowCoord, colCoord;
            boolean validMove = false;
            Scanner fromUser = new Scanner(System.in);
            System.out.println("Insert next move:");
            do {
                try {
                    System.out.print("\tRow coordinate: ");
//                while(!fromUser.hasNextInt()) fromUser.next();
//                rowCoord = fromUser.nextInt();
                    rowCoord = Utility.getAIntFromStdIn();
                    System.out.print("\tColumn coordinate: ");
//                while(!fromUser.hasNextInt()) fromUser.next();
//                colCoord = fromUser.nextInt();
                    colCoord = Utility.getAIntFromStdIn();
                    placeStone(new Coordinates(rowCoord, colCoord));
                    validMove = true;
                } catch (Board.PositionAlreadyOccupiedException e) {
                    System.err.print("Position already occupied.");
                } catch (IndexOutOfBoundsException e) {
                    System.err.print("Valid coordinates values are between " + 0 +
                            " and " + (getBoardSize() - 1) + " included.");
                } catch (IllegalArgumentException e) {
                    System.err.print("Invalid coordinates value.");
                } catch (Board.NoMoreEmptyPositionAvailableException e) {
                    e.printStackTrace();    // TODO : handle this exception
                } finally {
                    System.err.println();
                    System.err.flush();
                }
            } while (!validMove);
        }
    }
}
