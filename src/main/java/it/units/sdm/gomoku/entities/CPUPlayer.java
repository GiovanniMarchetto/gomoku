package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CPUPlayer extends Player {

    public CPUPlayer(@NotNull String name) {
        super(name);
    }

    @NotNull
    public Coordinates chooseRandomCoordinates(@NotNull Board currentBoard) {

        Random rand = new Random();

        final int MAX_ITERATION = 1_000;
        int boardSize = currentBoard.getSize();
        int x, y;
        Coordinates coordinates = null;

        for (int i = 0; i < MAX_ITERATION; i++) {
            x = rand.nextInt(boardSize);
            y = rand.nextInt(boardSize);
            coordinates = new Coordinates(x, y);
            if (currentBoard.getStoneAtCoordinates(coordinates).isNone())
                return coordinates;
        }

        int totalCases = 1;
        x = 0;
        y = 0;
        while (totalCases <= (Math.pow(boardSize,2))) {
            coordinates = new Coordinates(x, y);
            if (currentBoard.getStoneAtCoordinates(coordinates).isNone())
                return coordinates;

            y = (y == (boardSize - 1)) ? 0 : y + 1;
            if ((totalCases % boardSize) == 0) {
                x = x + 1;
            }

            totalCases++;
        }

        return coordinates;
    }

}

