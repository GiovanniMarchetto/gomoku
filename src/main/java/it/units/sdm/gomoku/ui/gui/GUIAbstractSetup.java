package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.support.AbstractSetup;
import org.jetbrains.annotations.NotNull;

public class GUIAbstractSetup extends AbstractSetup {

    public GUIAbstractSetup(@NotNull final Player playerOne, @NotNull final Player playerTwo,
                            @NotNull PositiveInteger numberOfGames, @NotNull PositiveInteger boardSizes) {
        super(playerOne, playerTwo, numberOfGames, boardSizes);
    }

}
