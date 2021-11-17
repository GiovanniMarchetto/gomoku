package it.units.sdm.gomoku.model.exceptions;

public class NoGameSetException extends Exception {
    public NoGameSetException() {
        super("Game not set");
    }
}
