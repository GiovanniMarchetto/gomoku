package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.ExposedEnum;
import it.units.sdm.gomoku.ui.support.MatchTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel.*;

public class CLIStartView extends View {// TODO : refactor this class

    public CLIStartView(StartViewmodel startViewmodel) {
        super(startViewmodel);
        switch (askAndGetNumberOfPlayers()) {
            case CPU_VS_CPU -> {
                firePropertyChange(player1NamePropertyName, null, "CPU1");
                firePropertyChange(player2NamePropertyName, null, "CPU2");
                firePropertyChange(player1CPUPropertyName, null, true);
                firePropertyChange(player2CPUPropertyName, null, true);
            }
            case PERSON_VS_CPU -> {
                firePropertyChange(player1NamePropertyName, null, askAndGetPlayerName(1));
                firePropertyChange(player2NamePropertyName, null, "CPU");
                firePropertyChange(player1CPUPropertyName, null, false);
                firePropertyChange(player2CPUPropertyName, null, true);
            }
            case PERSON_VS_PERSON -> {
                firePropertyChange(player1NamePropertyName, null, askAndGetPlayerName(1));
                firePropertyChange(player2NamePropertyName, null, askAndGetPlayerName(2));
                firePropertyChange(player1CPUPropertyName, null, false);
                firePropertyChange(player2CPUPropertyName, null, false);
            }
        }
        firePropertyChange(selectedBoardSizePropertyName, null, askAndGetBoardSize());
        firePropertyChange(numberOfGamesPropertyName, null, askAndGetNumberOfGames());

        ((StartViewmodel) getViewmodelAssociatedWithView()).startMatch();
    }

    @NotNull
    private static MatchTypes askAndGetNumberOfPlayers() {  // TODO : refactor this method
        String msgWithPossibleChoices = "Choose " + ExposedEnum.getEnumDescriptionOf(MatchTypes.class) + ": ";
        System.out.print("How many players? " + msgWithPossibleChoices);
        return Objects.requireNonNull(
                ExposedEnum.getEnumValueFromExposedValueOrNull(MatchTypes.class,
                        IOUtility.checkInputAndGet(
                                x -> ExposedEnum.getEnumValueFromExposedValueOrNull(
                                        MatchTypes.class, Objects.requireNonNull(x)) != null,
                                System.out,
                                msgWithPossibleChoices
                        )));
    }

    @NotNull
    private static String askAndGetPlayerName(int playerNumber) {
        System.out.print("Name of player " + playerNumber + ": ");
        return IOUtility.checkInputAndGet(
                x -> !x.isBlank(),
                System.out,
                "Specify a non blank name: "
        );
    }

    @NotNull
    private static String askAndGetBoardSize() {
        String msgWithPossibleChoices = "Choose the board size (" + ExposedEnum.getEnumDescriptionOf(BoardSizes.class) + "): ";
        System.out.print(msgWithPossibleChoices);
        int inputOption = Integer.parseInt(IOUtility.checkInputAndGet(
                insertedInput -> IOUtility.isInteger(insertedInput) &&
                        ExposedEnum.isValidExposedValueOf(BoardSizes.class, String.valueOf(insertedInput)),
                System.out,
                msgWithPossibleChoices
        ));
        return Objects.requireNonNull(
                ExposedEnum.getEnumValueFromExposedValueOrNull(BoardSizes.class, String.valueOf(inputOption))
        ).toString();
    }

    @NotNull
    private static String askAndGetNumberOfGames() {
        System.out.print("How many games? ");
        return IOUtility.checkInputAndGet(
                PositiveInteger::isPositiveInteger,
                System.out,
                "Insert a positive integer value for the number of games: "
        );
    }
}