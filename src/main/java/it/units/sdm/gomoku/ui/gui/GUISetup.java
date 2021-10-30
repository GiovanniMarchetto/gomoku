package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;

public class GUISetup extends Setup {

    public GUISetup(@NotNull final Player playerOne, @NotNull final Player playerTwo,
                    @NotNull PositiveInteger numberOfGames, @NotNull PositiveOddInteger boardSizes) {
        super(playerOne, playerTwo, numberOfGames, boardSizes);
    }

}
