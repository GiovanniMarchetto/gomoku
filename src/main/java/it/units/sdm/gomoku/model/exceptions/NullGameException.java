package it.units.sdm.gomoku.model.exceptions;

public class NullGameException extends Exception {
    public NullGameException() {
        super("Game is null");
    }
}
