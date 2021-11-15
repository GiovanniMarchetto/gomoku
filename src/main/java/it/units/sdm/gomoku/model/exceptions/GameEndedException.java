package it.units.sdm.gomoku.model.exceptions;

public class GameEndedException extends Exception {
    public GameEndedException() {
        super("The game is over.");
    }
}
