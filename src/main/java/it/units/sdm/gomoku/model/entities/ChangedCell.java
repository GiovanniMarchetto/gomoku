package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;

public class ChangedCell {
    private final Coordinates coordinates;
    private final Stone newStone;
    private final Stone oldStone;
    private final Board board;

    public ChangedCell(Coordinates coordinates, Stone newStone, Stone oldStone, Board board) {
        this.coordinates = coordinates;
        this.newStone = newStone;
        this.oldStone = oldStone;
        this.board = board;
    }

    public ChangedCell(Coordinates coordinates, Stone newStone, Board board) {
        this(coordinates, newStone, Stone.NONE, board);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Stone getNewStone() {
        return newStone;
    }

    public Stone getOldStone() {
        return oldStone;
    }

    public Board getBoard() {
        return board;
    }
}
