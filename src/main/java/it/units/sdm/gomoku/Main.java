package it.units.sdm.gomoku;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.ui.cli.OutputPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.Main.in;
import static it.units.sdm.gomoku.Main.out;
import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;
import static it.units.sdm.gomoku.model.custom_types.PositiveOddInteger.PositiveOddIntegerType;


enum BoardSizes implements ExposedEnum {
    VERY_SMALL(9, 1),
    SMALL(15, 2),
    NORMAL(19, 3),
    BIG(23, 4),
    VERY_BIG(29, 5);

    private final PositiveOddInteger boardSize;
    private final int ordinalValueOfThisEnum;

    BoardSizes(@PositiveOddIntegerType int boardSize, int ordinalValueOfThisEnum) {
        this.boardSize = new PositiveOddInteger(boardSize);
        this.ordinalValueOfThisEnum = ordinalValueOfThisEnum;
    }

    @NotNull
    public PositiveOddInteger getBoardSize() {
        return boardSize;
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(ordinalValueOfThisEnum);
    }

    @Override
    public String toString() {
        return
                name().replaceAll("_", " ");
    }
}

enum MatchTypes implements ExposedEnum {
    CPU_VS_PERSON(1),
    PERSON_VS_PERSON(2);
    private final NonNegativeInteger numberOfHumanPlayers;

    MatchTypes(@PositiveIntegerType int numberOfHumanPlayers) {
        this.numberOfHumanPlayers = new NonNegativeInteger(numberOfHumanPlayers);
    }

    @PositiveIntegerType
    public int getNumberOfHumanPlayers() {
        return numberOfHumanPlayers.intValue();
    }

    @Override
    public String toString() {
        return
                name().replaceAll("_", " ");
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(getNumberOfHumanPlayers());
    }
}

enum PlayerTypes {
    CPU,
    PERSON
}

enum BooleanAnswers implements ExposedEnum {
    YES('Y'),
    NO('N');

    private final char exposedValue;

    BooleanAnswers(char exposedValue) {
        this.exposedValue = exposedValue;
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(exposedValue);
    }
}

interface ExposedEnum {
    static <S extends ExposedEnum> boolean isValidExposedValueOf(@NotNull final Class<S> enumClazz, String exposedValueToCheck) {
        return getValuesOfEnum(Objects.requireNonNull(enumClazz))
                .stream()
                .anyMatch(doesExposedValueCorrespondToThisEnumValue(exposedValueToCheck));
    }

    @SuppressWarnings("unchecked")  // correct type checked before casting
    private static <T extends ExposedEnum> List<T> getValuesOfEnum(@NotNull final Class<T> enumClazz) {
        try {
            Object valuesAsObj;
            Object[] valuesAsObjArray;

            if (enumClazz.isEnum() &&
                    (valuesAsObj = enumClazz.getMethod("values").invoke(null))
                            .getClass().isArray() &&
                    Arrays.stream(valuesAsObjArray = (Object[]) valuesAsObj)
                            .filter(x -> x.getClass().isAssignableFrom(enumClazz))
                            .count() == valuesAsObjArray.length) {
                return Arrays.asList((T[]) valuesAsObjArray);   // correct type just checked before casting
            } else {
                throw new ClassCastException();
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
            Logger.getLogger(Main.class.getCanonicalName()).severe("Exception: " + e);
            return new ArrayList<>(0);
        }
    }

    @NotNull
    static <T extends ExposedEnum> String getEnumDescriptionOf(@NotNull final Class<T> enumClazz) {
        return getValuesOfEnum(enumClazz)
                .stream()
                .map(enumVal -> enumVal.getExposedValueOf() + " for " + enumVal)
                .collect(Collectors.joining(", "));
    }

    @NotNull
    private static <S extends ExposedEnum> Predicate<S> doesExposedValueCorrespondToThisEnumValue(String exposedValueToCheck) {
        return enumVal -> enumVal.getExposedValueOf().equals(exposedValueToCheck);
    }

    @Nullable
    static <S extends ExposedEnum> S getEnumValueFromExposedValueOrNull(@NotNull final Class<S> enumClazz, String exposedValue) {
        if (isValidExposedValueOf(Objects.requireNonNull(enumClazz), exposedValue)) {
            return getValuesOfEnum(enumClazz)
                    .stream().unordered().parallel()
                    .filter(doesExposedValueCorrespondToThisEnumValue(exposedValue))
                    .findAny()
                    .orElse(null);
        } else {
            return null;
        }
    }

    String getExposedValueOf();
}

class Setup {       // TODO :move outside

    @NotNull
    private final Map<Player, PlayerTypes> players;

    @NotNull
    private final PositiveInteger numberOfGames;

    @NotNull
    private final PositiveOddInteger boardSize;

    public Setup() throws IOException {
        MatchTypes matchType = askAndGetNumberOfPlayers();
        this.players = askAndGetPlayersOfThisMatch(matchType);
        this.numberOfGames = askAndGetNumberOfGames();
        this.boardSize = askAndGetBoardSize();
    }

    public static int getAnIntFromInput() {
        try {
            return Integer.parseInt(in.nextLine().replace("\n", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static char getACharFromInput() {
        try {
            String line = in.nextLine().replace("\n", "");
            return line.charAt(line.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            return '\n';
        }
    }

    public static <T> T checkInputAndGet(
            @NotNull final Supplier<T> inputSupplier,
            @NotNull final Predicate<T> validator,
            @NotNull final String messageErrorIfInvalid) throws IOException {
        return checkInputAndGet(inputSupplier, validator, messageErrorIfInvalid, IllegalArgumentException.class);
    }

    public static <T> T checkInputAndGet(
            @NotNull final Supplier<T> inputSupplier,
            @NotNull final Predicate<T> validator,
            @NotNull final String messageErrorIfInvalid,
            @NotNull final Class<? extends Throwable> throwable) throws IOException {
        T inputValue = null;
        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                inputValue = inputSupplier.get();
            } catch (InputMismatchException ignored) {
            } catch (Exception e) {
                if (!throwable.isAssignableFrom(e.getClass())) {
                    throw e;
                }
            }
            isValidInput = validator.test(inputValue);
            if (!isValidInput) {
                out.print("Invalid input. " + messageErrorIfInvalid);
            }
        }
        return inputValue;
    }

    @NotNull
    @Length(length = 2)
    public Player[] getPlayers() {
        return players.keySet().toArray(new Player[0]);
    }

    @NotNull
    public PositiveInteger getNumberOfGames() {
        return numberOfGames;
    }

    @NotNull
    public PositiveOddInteger getBoardSizeValue() {
        return boardSize;
    }

    @Override
    public String toString() {
        return "Setup{" +
                "players=" + players +
                ", numberOfGames=" + numberOfGames +
                ", boardSize=" + boardSize +
                '}';
    }

    @NotNull
    private Map<Player, PlayerTypes> askAndGetPlayersOfThisMatch(@NotNull final MatchTypes matchType) throws IOException {
        List<String> playerNames = new ArrayList<>(Objects.requireNonNull(matchType).getNumberOfHumanPlayers());
        for (int i = 1; i <= matchType.getNumberOfHumanPlayers(); i++) {
            out.print("Name of player" + (matchType.getNumberOfHumanPlayers() > 1 ? " " + i : "") + ": ");
            playerNames.add(
                    checkInputAndGet(
                            in::nextLine,
                            x -> !x.isBlank() && !playerNames.contains(x),
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
    private MatchTypes askAndGetNumberOfPlayers() throws IOException {
        String msgWithPossibleChoices = "Choose " + ExposedEnum.getEnumDescriptionOf(MatchTypes.class) + ": ";
        out.print("How many players? " + msgWithPossibleChoices);
        return checkInputAndGet(
                () -> ExposedEnum.getEnumValueFromExposedValueOrNull(MatchTypes.class, String.valueOf(getAnIntFromInput())),
                Objects::nonNull,
                msgWithPossibleChoices
        );
    }

    @NotNull
    private PositiveInteger askAndGetNumberOfGames() throws IOException {
        out.print("How many games? ");
        return checkInputAndGet(
                () -> new PositiveInteger(getAnIntFromInput()),
                Objects::nonNull,
                "Insert a positive integer value for the number of games: "
        );
    }

    @NotNull
    private PositiveOddInteger askAndGetBoardSize() throws IOException {
        String msgWithPossibleChoices = "Choose the board size (" + ExposedEnum.getEnumDescriptionOf(BoardSizes.class) + "): ";
        out.print(msgWithPossibleChoices);
        int inputOption = checkInputAndGet(
                Setup::getAnIntFromInput,
                insertedInput -> Objects.nonNull(insertedInput) &&
                        ExposedEnum.isValidExposedValueOf(BoardSizes.class, String.valueOf(insertedInput)),
                msgWithPossibleChoices
        );
        return Objects.requireNonNull(
                ExposedEnum.getEnumValueFromExposedValueOrNull(BoardSizes.class, String.valueOf(inputOption))
        ).getBoardSize();
    }

}

public class Main {

    public static final OutputPrinter out = OutputPrinter.getInstance(Charset.defaultCharset());
    public static final Scanner in = new Scanner(System.in);

    @NotNull
    private static Coordinates waitForAValidMoveOfAPlayerAndGet(
            @NotNull final Game game,
            @NotNull final PositiveOddInteger boardSizes) throws IOException {

        out.println("Next move");
        return Setup.checkInputAndGet(                                                      // TODO : move static method outside Setup class
                () -> {
                    String[] coordNames = {"row position", "column position"};
                    NonNegativeInteger[] xyCoords = new NonNegativeInteger[coordNames.length];
                    for (int i = 0; i < xyCoords.length; i++) {
                        try {
                            out.print("\tInsert " + coordNames[i] + ": ");
                            xyCoords[i] = Setup.checkInputAndGet(                                // TODO : move static method outside Setup class
                                    () -> new NonNegativeInteger(Setup.getAnIntFromInput()),     // TODO : move static method outside Setup class
                                    coord -> coord.compareTo(boardSizes) < 0,       // TODO : predicate to validate a single coordinate to be added in class Board
                                    "Insert a valid coordinate: "
                            );
                        } catch (IOException e) {
                            Logger.getLogger(Main.class.getCanonicalName())
                                    .severe("Exception: " + e);                             // TODO : refactor: move all duplications of Logger.getLogger() in a single utility function in utils package
                        }
                    }
                    return new Coordinates(xyCoords);
                },
                coordinates -> game.getBoard().getStoneAtCoordinates(coordinates).isNone(),     // TODO : message chain code smell
                "The position for the specified coordinates is already occupied on the board. Re-insert the coordinates:\n"
        );

    }


    public static void main(String[] args) throws IOException {

        Setup setup = new Setup();
        Match matchToDispute = new Match(setup.getBoardSizeValue(), setup.getNumberOfGames(), setup.getPlayers());

        for (int nGame = 1; nGame <= matchToDispute.getHowManyGames(); nGame++) {
            out.createNewSection();

            try {

                out.createNewSection();
                out.println("New game!");
                Game currentGame = matchToDispute.startNewGame();

                boolean continuePlaying = true;
                while (continuePlaying) {
                    out.createNewSection();
                    out.println(currentGame.getBoard());
                    Arrays.stream(new Player[]{matchToDispute.getCurrentBlackPlayer(), matchToDispute.getCurrentWhitePlayer()})
                            .forEachOrdered(player -> {
                                try {
                                    Match.executeMoveOfPlayerInGame(player, currentGame, player instanceof CPUPlayer ? null : waitForAValidMoveOfAPlayerAndGet(currentGame, setup.getBoardSizeValue()));
                                } catch (Board.PositionAlreadyOccupiedException e) {
                                    // Should never happen (already checked the input)
                                    try {
                                        out.println("Choose an unoccupied position!");
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                } catch (Board.NoMoreEmptyPositionAvailableException e) {
                                    // game ended, will stop at the next iteration
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                    continuePlaying = !currentGame.isThisGameEnded();
                    if (continuePlaying) {
                        out.clearLastSection();
                    }
                }
                out.print("Game ended! ");
                try {
                    out.println(currentGame.getWinner() + " won!");
                } catch (Game.NotEndedGameException e) {
                    out.println("It's a draw!");
                }

                handleADrawIfIsLastGame(matchToDispute);

                if (nGame < matchToDispute.getHowManyGames()) {
                    out.print("Press enter to start the new game");
                    in.nextLine();
                    out.clearLastSection();
                } else {
                    out.println("\nMatch ended");
                    out.println("Score: " + matchToDispute.getScore());
                }

            } catch (IOException e) {
                Logger.getLogger(Main.class.getCanonicalName())
                        .severe("Exception in class " + Main.class.getCanonicalName() + ": " + e);
            }
        }

    }

    private static void handleADrawIfIsLastGame(Match matchToDispute) throws IOException {  // TODO : not tested
        if (matchToDispute.isEndedWithADraft()) {
            out.print("It's a draw!\n" +
                    "Would you like to play an additional game? " + ExposedEnum.getEnumDescriptionOf(BooleanAnswers.class) + ": ");

            char response = Setup.checkInputAndGet(
                    () -> Character.toUpperCase(Setup.getACharFromInput()),
                    answer -> ExposedEnum.isValidExposedValueOf(BooleanAnswers.class, String.valueOf(answer)),
                    "Insert a valid answer: " + ExposedEnum.getEnumDescriptionOf(BooleanAnswers.class) + ": "
            );
            if (Objects.requireNonNull(ExposedEnum.getEnumValueFromExposedValueOrNull(BooleanAnswers.class, String.valueOf(response)))
                    == BooleanAnswers.YES) {
                matchToDispute.addAGame();
            }
        }
    }

}