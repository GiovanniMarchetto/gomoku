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

public class CLIStartView extends View<StartViewmodel> {// TODO : refactor this class and test

    public CLIStartView(StartViewmodel startViewmodel) {
        super(startViewmodel);
        switch (askAndGetNumberOfPlayers()) {   // TODO : all tests are missing (see GUIStartViewTest)
            case CPU_VS_CPU -> {
                getViewmodelAssociatedWithView().setPlayer1Name("CPU1");
                getViewmodelAssociatedWithView().setPlayer2Name("CPU2");
                getViewmodelAssociatedWithView().setPlayer1CPU(true);
                firePropertyChange(player2CPUPropertyName, true);
            }
            case PERSON_VS_CPU -> {
                getViewmodelAssociatedWithView().setPlayer1Name(askAndGetPlayerName(1));
                getViewmodelAssociatedWithView().setPlayer2Name("CPU");
                getViewmodelAssociatedWithView().setPlayer1CPU(false);
                firePropertyChange(player2CPUPropertyName, true);
            }
            case PERSON_VS_PERSON -> {
                getViewmodelAssociatedWithView().setPlayer1Name(askAndGetPlayerName(1));
                getViewmodelAssociatedWithView().setPlayer2Name(askAndGetPlayerName(2));
                getViewmodelAssociatedWithView().setPlayer1CPU(false);
                firePropertyChange(player2CPUPropertyName, false);
            }
        }
        firePropertyChange(selectedBoardSizePropertyName, askAndGetBoardSize());
        firePropertyChange(numberOfGamesPropertyName, askAndGetNumberOfGames());

        getViewmodelAssociatedWithView().startMatch();
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
                PositiveInteger::isPositiveIntegerFromString,
                System.out,
                "Insert a positive integer value for the number of games: "
        );
    }
}
