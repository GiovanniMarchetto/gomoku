package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.support.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CLISetup extends Setup { // TODO : to be tested

    public CLISetup() {
        super(
                askAndGetPlayersOfThisMatch(askAndGetNumberOfPlayers()),
                askAndGetNumberOfGames(),
                askAndGetBoardSize()
        );
    }

    private static @NotNull Player[] askAndGetPlayersOfThisMatch(@NotNull final MatchTypes matchType) {
        List<String> playerNames = new ArrayList<>(Objects.requireNonNull(matchType).getNumberOfHumanPlayers());
        for (int i = 1; i <= matchType.getNumberOfHumanPlayers(); i++) {
            System.out.print("Name of player" + (matchType.getNumberOfHumanPlayers() > 1 ? " " + i : "") + ": ");
            playerNames.add(
                    IOUtility.checkInputAndGet(
                            x -> !x.isBlank() && !playerNames.contains(x),
                            System.out,
                            "Specify a non blank and not already inserted name: "
                    )
            );
        }
        return switch (matchType) {
            case CPU_VS_PERSON -> new Player[]{new Player(playerNames.get(0)), new CPUPlayer()};
            case PERSON_VS_PERSON -> new Player[]{new Player(playerNames.get(0)),new Player(playerNames.get(1))};
            //noinspection UnnecessaryDefault   // default branch used as good practice
            default -> throw new IllegalArgumentException("Unexpected " + matchType);
        };
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
    private static PositiveInteger askAndGetNumberOfGames() {
        System.out.print("How many games? ");
        return new PositiveInteger(Integer.parseInt(IOUtility.checkInputAndGet(
                IOUtility::isInteger,
                System.out,
                "Insert a positive integer value for the number of games: "
        )));
    }

    @NotNull
    private static PositiveOddInteger askAndGetBoardSize() {
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
        ).getBoardSize();
    }
}
