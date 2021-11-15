package it.units.sdm.gomoku.model.exceptions;

public class MatchNotEndedException extends Exception {
    public MatchNotEndedException() {
        super("Match not ended yet!");
    }
}
