package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.model.exceptions.NoGameSetException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove() throws NoGameSetException {  // TODO: test + refactor
        Game currentGame = getCurrentGame();
        if (currentGame != null) {
            Utility.runOnSeparateThread(() -> {//TODO: separate thread in model?
                Objects.requireNonNull(getCurrentGame());
                setCoordinatesRequired(true);
                try {//TODO:temporary
                    super.makeMove();
                } catch (NoGameSetException ignored) {
                }
                setCoordinatesRequired(false);
            });
        } else {
            throw new NoGameSetException();
        }
    }

}
