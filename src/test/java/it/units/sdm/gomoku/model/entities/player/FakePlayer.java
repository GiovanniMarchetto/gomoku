package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.entities.Player;
import org.jetbrains.annotations.NotNull;

public class FakePlayer extends Player {
    protected FakePlayer(@NotNull String playerName) {
        super(playerName);
    }
}
