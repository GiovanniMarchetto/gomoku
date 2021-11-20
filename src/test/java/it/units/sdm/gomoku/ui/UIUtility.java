package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Setup;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class UIUtility {

    @NotNull
    public static Stream<Arguments> setupsSupplierAndFlagIfValid() {   // TODO : add a number of valid/invalid setups
        return Stream.of(
                Arguments.of(
                        new Setup(
                                new HumanPlayer("One"),
                                new HumanPlayer("Two"),
                                new PositiveInteger(1),
                                BoardSizes.NORMAL.getBoardSize()),
                        true));
    }

}
