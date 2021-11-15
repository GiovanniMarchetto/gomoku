package it.units.sdm.gomoku.model.exceptions;

public class MaxNumberOfGamesException extends Exception {
    public MaxNumberOfGamesException() {
        super("All the games provided by the match have been played");
    }
}
