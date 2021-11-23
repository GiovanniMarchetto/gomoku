package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.ExposedEnum;
import it.units.sdm.gomoku.ui.support.MatchTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class CLIStartView extends View<StartViewmodel> {

    public CLIStartView(StartViewmodel startViewmodel) {
        super(startViewmodel);
    }

    private static String askAndGetCPUPlayerSkillFactor(int playerNumber) {
        Predicate<String> isValidSkillFactorFromString = value -> {
            try {
                return CPUPlayer.isValidSkillFactor(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                return false;
            }
        };

        System.out.print("Skill factor of player " + playerNumber + " (between 0 and 1): ");
        return IOUtility.checkInputAndGet(
                isValidSkillFactorFromString,
                System.out,
                "Specify a double between " + CPUPlayer.MIN_SKILL_FACTOR
                        + " and " + CPUPlayer.MAX_SKILL_FACTOR + " : "
        );
    }

    @NotNull
    private static MatchTypes askAndGetNumberOfPlayers() {
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

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();

        switch (askAndGetNumberOfPlayers()) {
            case CPU_VS_CPU -> getViewmodelAssociatedWithView()
                    .setPlayer1Name("CPU1")
                    .setPlayer1CPUSkillFactor(askAndGetCPUPlayerSkillFactor(1))
                    .setPlayer2Name("CPU2")
                    .setPlayer2CPUSkillFactor(askAndGetCPUPlayerSkillFactor(2))
                    .setPlayer1CPU(true)
                    .setPlayer2CPU(true);
            case PERSON_VS_CPU -> getViewmodelAssociatedWithView()
                    .setPlayer1Name(askAndGetPlayerName(1))
                    .setPlayer2Name("CPU")
                    .setPlayer2CPUSkillFactor(askAndGetCPUPlayerSkillFactor(2))
                    .setPlayer1CPU(false)
                    .setPlayer2CPU(true);
            case PERSON_VS_PERSON -> getViewmodelAssociatedWithView()
                    .setPlayer1Name(askAndGetPlayerName(1))
                    .setPlayer2Name(askAndGetPlayerName(2))
                    .setPlayer1CPU(false)
                    .setPlayer2CPU(false);
        }
        getViewmodelAssociatedWithView()
                .setSelectedBoardSize(askAndGetBoardSize())
                .setNumberOfGames(askAndGetNumberOfGames())
                .createAndStartMatch();
    }
}
