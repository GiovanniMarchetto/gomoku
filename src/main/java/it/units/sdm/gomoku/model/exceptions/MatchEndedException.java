package it.units.sdm.gomoku.model.exceptions;

public class MatchEndedException extends Exception {
    public MatchEndedException() {
        super("Match already ended!");
    }
}
