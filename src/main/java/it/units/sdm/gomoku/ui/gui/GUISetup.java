package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.support.PlayerTypes;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GUISetup extends Setup {

    public GUISetup(@NotNull Map<Player, PlayerTypes> players, @NotNull PositiveInteger numberOfGames, @NotNull PositiveOddInteger boardSizes) {
        super(players, numberOfGames, boardSizes);
    }

}
