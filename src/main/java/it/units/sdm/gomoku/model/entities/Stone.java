package it.units.sdm.gomoku.model.entities;

public record Stone(Color color) {

    public enum Color {
        BLACK,
        WHITE;
    }
}
