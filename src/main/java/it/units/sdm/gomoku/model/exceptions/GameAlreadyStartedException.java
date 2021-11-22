package it.units.sdm.gomoku.model.exceptions;

public class GameAlreadyStartedException extends Exception {
    public GameAlreadyStartedException() {
        super("Game already set.");
    }
}
