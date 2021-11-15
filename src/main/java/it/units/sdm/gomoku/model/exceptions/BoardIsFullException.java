package it.units.sdm.gomoku.model.exceptions;

public class BoardIsFullException extends Exception {
    public BoardIsFullException() {
        super("The board is entirely filled. No more space available.");
    }
}
