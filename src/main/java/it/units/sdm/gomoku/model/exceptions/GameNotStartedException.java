package it.units.sdm.gomoku.model.exceptions;

public class GameNotStartedException extends Exception {
    public GameNotStartedException() {
        super("Game not started");
    }
}
