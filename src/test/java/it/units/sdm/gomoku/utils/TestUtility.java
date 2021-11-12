package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.ui.support.MoveControlRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.IOUtility.CSV_NEW_LINE;
import static it.units.sdm.gomoku.utils.IOUtility.CSV_SEPARATOR;
import static it.units.sdm.gomoku.utils.Predicates.isNonEmptyString;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtility {

    // TODO : re-see tests of this class

    public final static String END_GAMES = "/endGames.json";

    @NotNull    // TODO : BoardTestUtility class may needed to contain all the method to support BoardTest
    public static Board createBoardFromCellMatrix(Cell[][] cellMatrix, PositiveInteger boardSize) {
        Board board = new Board(boardSize);
        IntStream.range(0, boardSize.intValue())
                .forEach(x -> IntStream.range(0, boardSize.intValue())
                        .filter(y -> !cellMatrix[x][y].isEmpty())
                        .forEach(y -> {
                            try {
                                //noinspection ConstantConditions // just filtered out
                                board.occupyPosition(cellMatrix[x][y].getStone().color(), new Coordinates(x, y));
                            } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Board.CellOutOfBoardException e) {
                                fail(e);
                            }
                        }));
        return board;
    }

    @NotNull
    public static Board createBoardFromCellMatrix(Cell[][] cellMatrix, int boardSize) {
        return createBoardFromCellMatrix(cellMatrix, new PositiveInteger(boardSize));
    }

    @NotNull
    public static Cell[][] readBoardOfCellsFromCSVFile(@NotNull String filePath) {

        Function<String[][], Cell[][]> convertStringMatrixToCellMatrix = stringMatrix ->
                Arrays.stream(stringMatrix).sequential()
                        .map(aLine -> Arrays.stream(aLine)
                                .map(TestUtility::getCellFromStoneRepresentedAsString)
                                .toArray(Cell[]::new))
                        .toArray(Cell[][]::new);

        try {
            String[][] boardAsMatrixOfStrings = IOUtility.readFromCsvToStringMatrix(Objects.requireNonNull(filePath));
            return convertStringMatrixToCellMatrix.apply(boardAsMatrixOfStrings);
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Cell[0][0];
        }
    }

    @NotNull
    private static Cell getCellFromStoneRepresentedAsString(@NotNull final String stoneColorAsString) {
        Cell cell = new Cell();
        try {
            cell.setStone(new Stone(Stone.Color.valueOf(stoneColorAsString)));
        } catch (IllegalArgumentException ignored) {
        }
        return cell;
    }

    @NotNull
    public static String createNxNRandomBoardToStringInCSVFormat(int N, int randomSeed) {
        Random random = new Random(randomSeed);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (random.nextInt(3)) {
                    case 0 -> s.append(Stone.Color.BLACK);
                    case 1 -> s.append(Stone.Color.WHITE);
                    default -> s.append("null");
                }
                if (j < N - 1) {
                    s.append(CSV_SEPARATOR);
                } else if (i < N - 1) {
                    s.append(CSV_NEW_LINE);
                }
            }
        }

        return s.toString();
    }

    @NotNull
    public static String createNxNRandomBoardToStringInCSVFormat(int N) {
        final int RANDOM_SEED = 0;
        return createNxNRandomBoardToStringInCSVFormat(N, RANDOM_SEED);
    }

    @NotNull
    public static Stream<Arguments> provideCoupleOfIntegersInRange(int startIncluded, int endExcluded) {
        return IntStream.range(startIncluded, endExcluded)
                .unordered().parallel()
                .boxed()
                .flatMap(i -> IntStream.range(startIncluded, endExcluded).mapToObj(j -> Arguments.of(i, j)));
    }

    @NotNull
    public static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillNExcluded(int N) {
        return provideCoupleOfIntegersInRange(0, N);
    }

    @NotNull
    public static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillBoardSize() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(EnvVariables.BOARD_SIZE.intValue());
    }

    @NotNull
    public static Stream<Arguments> getStreamOfMoveControlRecordFields() {
        return getStreamOfMoveControlRecordFieldsFromJSON(END_GAMES);
    }

    @NotNull
    public static Stream<Arguments> getStreamOfMoveControlRecordFieldsFromJSON(@NotNull String filePath) {
        try {
            String json = Files.readString(Paths.get(Objects.requireNonNull(TestUtility.class.getResource(filePath)).toURI()));
            JSONObject jsonObject = new JSONObject(json);

            return ((JSONArray) jsonObject.get("data")).toList()
                    .stream().unordered().parallel()
                    .map(argsForOneTest -> {
                        Map<?, ?> argsMap = (HashMap<?, ?>) argsForOneTest;

                        List<Integer> ints = ((List<?>) argsMap.get("coordinates")).stream().map(x -> (int) x).collect(Collectors.toList());

                        MoveControlRecord moveControlRecord = new MoveControlRecord(
                                ((List<?>) (argsMap).get("matrix"))
                                        .stream().sequential()
                                        .map(row -> ((List<?>) row)
                                                .stream()
                                                .map(obj -> (String) obj)
                                                .map(TestUtility::getCellFromStoneRepresentedAsString)
                                                .toArray(Cell[]::new))
                                        .toArray(Cell[][]::new),
                                new Coordinates(ints.get(0), ints.get(1)),
                                (boolean) argsMap.get("expected"),
                                (boolean) argsMap.get("finishedGame")
                        );

                        return Arguments.of(
                                moveControlRecord.matrix(),
                                moveControlRecord.coordinatesToControl(),
                                moveControlRecord.isWinChainFromCoordinates(),
                                moveControlRecord.isFinishedGame());
                    });
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return Stream.empty();
        }
    }

    public static int getTotalNumberOfValidStoneInTheGivenBoardAsStringInCSVFormat(@NotNull final String boardAsCSVString) {
        Map<Stone, Integer> countStonesPerType = getHowManyStonesPerColorAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsCSVString));
        return countStonesPerType.values().stream().mapToInt(Integer::intValue).sum();
    }

    @NotNull
    private static Map<Stone, Integer> getHowManyStonesPerColorAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringMatrix) {
        Stone[] possibleStoneTypes_equivalencyClass =
                new Stone[]{new Stone(Stone.Color.BLACK), new Stone(Stone.Color.WHITE), null};
        return Arrays.stream(possibleStoneTypes_equivalencyClass)
                .unordered().parallel()
                .map(stoneType -> new AbstractMap.SimpleEntry<>(
                        stoneType,
                        (int) getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsStringMatrix))
                                .flatMap(aCell -> aCell)
                                .map(TestUtility::getCellFromStoneRepresentedAsString)
                                .map(Cell::getStone)
                                .filter(stoneThisCell -> (stoneThisCell == null && stoneType == null)
                                        || (stoneThisCell != null && stoneThisCell.equals(stoneType)))
                                .count()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public static Stream<Stream<String>> getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringInCSVFormat) {
        return Arrays.stream(Objects.requireNonNull(boardAsStringInCSVFormat).split(CSV_NEW_LINE))
                .filter(isNonEmptyString)
                .map(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR)));
    }


    @NotNull
    public static Field getFieldAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                      @NotNull final String fieldName)  // TODO : test
            throws NoSuchFieldException {   // TODO : use this method wherever needed
        return (Field) getMemberAlreadyMadeAccessible(Objects.requireNonNull(clazz), fieldName, null);
    }

    @NotNull
    public static Method getMethodAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                        @NotNull final String methodName,
                                                        @NotNull Class<?>[] parameterTypes)  // TODO : test
            throws NoSuchFieldException {   // TODO : use this method wherever needed
        return (Method) getMemberAlreadyMadeAccessible(
                Objects.requireNonNull(clazz),
                Objects.requireNonNull(methodName),
                Objects.requireNonNull(parameterTypes));
    }

    @NotNull
    public static AccessibleObject getMemberAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                                  @NotNull final String fieldName,
                                                                  @Nullable Class<?>[] parameterTypesIfMethodOrNullIfField)  // TODO : test
            throws NoSuchFieldException {   // TODO : use this method wherever needed
        boolean trueIfFieldsDesiredOrFalseForMethods = parameterTypesIfMethodOrNullIfField == null;
        AccessibleObject accessibleObject = getInheritedFieldsOrMethods(Objects.requireNonNull(clazz), trueIfFieldsDesiredOrFalseForMethods)
                .stream()
                .map(member -> trueIfFieldsDesiredOrFalseForMethods ? (Field) member : (Method) member)
                .filter(aMember -> aMember.getName().equals(Objects.requireNonNull(fieldName)))
                .filter(aMember -> trueIfFieldsDesiredOrFalseForMethods || Arrays.equals(((Method) aMember).getParameterTypes(), parameterTypesIfMethodOrNullIfField))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchFieldException(fieldName + " member not found neither in class " +
                                clazz.getCanonicalName() + " nor in its superclasses."));
        accessibleObject.setAccessible(true);
        return accessibleObject;
    }

    @NotNull
    private static List<AccessibleObject> getInheritedFieldsOrMethods(@Nullable Class<?> clazz,
                                                                      boolean trueIfFieldsDesiredOrFalseForMethods) {
        List<AccessibleObject> result = new ArrayList<>();
        for (Class<?> derivedClass = clazz;
             derivedClass != null && derivedClass != Object.class;
             derivedClass = derivedClass.getSuperclass()) {
            Collections.addAll(
                    result,
                    trueIfFieldsDesiredOrFalseForMethods
                            ? derivedClass.getDeclaredFields()
                            : derivedClass.getDeclaredMethods());
        }
        return result;
    }

    @Nullable
    public static <T> Object getFieldValue(@NotNull final String fieldName, @NotNull final T objectInstance)
            throws NoSuchFieldException, IllegalAccessException {   // TODO : test
        return getFieldAlreadyMadeAccessible(
                Objects.requireNonNull(objectInstance).getClass(), Objects.requireNonNull(fieldName))
                .get(objectInstance);
    }

    public static <T, S> void setFieldValue(
            @NotNull final String fieldName, @Nullable final S newValue, @NotNull final T objectInstance)
            throws NoSuchFieldException, IllegalAccessException {   // TODO : test
        getFieldAlreadyMadeAccessible(
                Objects.requireNonNull(objectInstance).getClass(), Objects.requireNonNull(fieldName))
                .set(objectInstance, newValue);
    }

    public static <T> Object invokeMethodOnObject(
            @NotNull final T targetObject, @NotNull final String methodName, @Nullable Object... paramsToMethod)
            throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {  // TODO : test
        Class<?>[] paramTypes =
                Arrays.stream(paramsToMethod).sequential()
                        .filter(Objects::nonNull)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new);
        return getMethodAlreadyMadeAccessible(
                targetObject.getClass(), methodName, paramTypes)
                .invoke(targetObject, paramsToMethod);
    }

    @NotNull
    public static String trimAndCapitalizeFirstLetterAndGetOrDoNothingIfEmpty(@NotNull final String inputString) {
        // TODO : test
        String outputString = inputString.trim();
        if (outputString.isBlank()) {
            return inputString;
        }
        return outputString.substring(0, 1).toUpperCase() +
                (outputString.length() > 1 ? inputString.substring(1) : "");
    }

    @NotNull
    public static String getStringDifferentFromGivenOne(String inputString) {   // TODO : test
        return inputString + "_whateverTrailingStringJustToMakeTheGivenStringDifferent";
    }
}