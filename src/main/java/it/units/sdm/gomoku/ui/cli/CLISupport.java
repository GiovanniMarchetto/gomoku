package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.ui.cli.io.InputReader;
import it.units.sdm.gomoku.ui.cli.io.OutputPrinter;
import it.units.sdm.gomoku.ui.support.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CLISupport extends Setup { // TODO : to be tested

    private final static OutputPrinter out = OutputPrinter.getInstance();
    private final static InputReader in = InputReader.getInstance();

    public CLISupport() throws IOException {
        super(
                askAndGetPlayersOfThisMatch(askAndGetNumberOfPlayers()),
                askAndGetNumberOfGames(),
                askAndGetBoardSize()
        );
    }

    @NotNull
    private static Map<Player, PlayerTypes> askAndGetPlayersOfThisMatch(@NotNull final MatchTypes matchType) throws IOException {
        List<String> playerNames = new ArrayList<>(Objects.requireNonNull(matchType).getNumberOfHumanPlayers());
        for (int i = 1; i <= matchType.getNumberOfHumanPlayers(); i++) {
            out.print("Name of player" + (matchType.getNumberOfHumanPlayers() > 1 ? " " + i : "") + ": ");
            playerNames.add(
                    InputReader.checkInputAndGet(
                            in::nextLine,
                            x -> !x.isBlank() && !playerNames.contains(x),
                            out,
                            "Specify a non blank and not already inserted name: "
                    )
            );
        }
        return switch (matchType) {
            case CPU_VS_PERSON -> Stream.of(
                    new AbstractMap.SimpleEntry<>(new Player(playerNames.get(0)), PlayerTypes.PERSON),
                    new AbstractMap.SimpleEntry<>(new CPUPlayer(), PlayerTypes.CPU)
            ).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
            case PERSON_VS_PERSON -> playerNames.stream().sequential()
                    .map(playerName -> new AbstractMap.SimpleEntry<>(new Player(playerName), PlayerTypes.PERSON))
                    .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
            //noinspection UnnecessaryDefault   // default branch used as good practice
            default -> throw new IllegalArgumentException("Unexpected " + matchType);
        };
    }

    @NotNull
    private static MatchTypes askAndGetNumberOfPlayers() throws IOException {
        String msgWithPossibleChoices = "Choose " + ExposedEnum.getEnumDescriptionOf(MatchTypes.class) + ": ";
        out.print("How many players? " + msgWithPossibleChoices);
        return InputReader.checkInputAndGet(
                () -> ExposedEnum.getEnumValueFromExposedValueOrNull(MatchTypes.class, String.valueOf(in.getAnIntFromInput())),
                Objects::nonNull,
                out,
                msgWithPossibleChoices
        );
    }

    @NotNull
    private static PositiveInteger askAndGetNumberOfGames() throws IOException {
        out.print("How many games? ");
        return InputReader.checkInputAndGet(
                () -> new PositiveInteger(in.getAnIntFromInput()),
                Objects::nonNull,
                out,
                "Insert a positive integer value for the number of games: "
        );
    }

    @NotNull
    private static PositiveOddInteger askAndGetBoardSize() throws IOException {
        String msgWithPossibleChoices = "Choose the board size (" + ExposedEnum.getEnumDescriptionOf(BoardSizes.class) + "): ";
        out.print(msgWithPossibleChoices);
        int inputOption = InputReader.checkInputAndGet(
                in::getAnIntFromInput,
                insertedInput -> Objects.nonNull(insertedInput) &&
                        ExposedEnum.isValidExposedValueOf(BoardSizes.class, String.valueOf(insertedInput)),
                out,
                msgWithPossibleChoices
        );
        return Objects.requireNonNull(
                ExposedEnum.getEnumValueFromExposedValueOrNull(BoardSizes.class, String.valueOf(inputOption))
        ).getBoardSize();
    }
}
