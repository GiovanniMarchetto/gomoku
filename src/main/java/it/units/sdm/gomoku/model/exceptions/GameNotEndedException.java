package it.units.sdm.gomoku.model.exceptions;

public class GameNotEndedException extends Exception {
    public GameNotEndedException() {
        super("The game is not over.");
    }
}
