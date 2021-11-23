package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.Predicates.isNonEmptyString;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtility {

    // TODO : re-see tests of this class

    public final static String END_GAMES = "/endGames.json";
    public final static String CSV_SEPARATOR = ",";
    public final static String CSV_NEW_LINE = "\n";
    private final static char CSV_COMMENTED_LINE_INDICATOR = '#';

    // TODO : BoardTestUtility class may needed to contain all the method to support BoardTest

    @NotNull
    public static Board createBoardFromCellMatrix(Cell[][] cellMatrix) {
        PositiveInteger boardSize = new PositiveInteger(cellMatrix.length);
        Board board = new Board(boardSize);
        IntStream.range(0, boardSize.intValue())
                .forEach(x -> IntStream.range(0, boardSize.intValue())
                        .filter(y -> !cellMatrix[x][y].isEmpty())
                        .forEach(y -> {
                            try {
                                //noinspection ConstantConditions // just filtered out
                                board.occupyPosition(cellMatrix[x][y].getStone().getColor(), new Coordinates(x, y));    // TODO: message chain
                            } catch (BoardIsFullException | CellAlreadyOccupiedException | CellOutOfBoardException e) {
                                fail(e);
                            }
                        }));
        return board;
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
            String[][] boardAsMatrixOfStrings = readFromCsvToStringMatrix(Objects.requireNonNull(filePath));
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
            cell.setStoneFromColor(Color.valueOf(stoneColorAsString));
        } catch (IllegalArgumentException ignored) {
        }
        return cell;
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
                new Stone[]{new Stone(Color.BLACK), new Stone(Color.WHITE), null};
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


    //region Reflection Utils
    @NotNull
    public static Field getFieldAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                      @NotNull final String fieldName)
            throws NoSuchFieldException {
        return (Field) getMemberAlreadyMadeAccessible(Objects.requireNonNull(clazz), fieldName, null);
    }

    @NotNull
    public static Method getMethodAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                        @NotNull final String methodName,
                                                        @Nullable Class<?>... methodParameterTypes)
            throws NoSuchFieldException {
        // WARNING: method not working if parameters are abstract class and derived classes are passed
        return (Method) getMemberAlreadyMadeAccessible(
                Objects.requireNonNull(clazz),
                Objects.requireNonNull(methodName),
                methodParameterTypes);
    }

    @NotNull
    public static AccessibleObject getMemberAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                                  @NotNull final String fieldName,
                                                                  @Nullable Class<?>[] parameterTypesIfMethodOrNullIfField)
            throws NoSuchFieldException {
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
        return getMethodAlreadyMadeAccessible(targetObject.getClass(), methodName, paramTypes)
                .invoke(targetObject, paramsToMethod);
    }
    //endregion Reflection Utils

    public static <T> long getNumberOfNullFieldsOfObjectWhichNameIsNotInList(
            @NotNull final List<String> listOfNamesOfNullableFields, @NotNull final T targetObject) {   // TODO: test
        Objects.requireNonNull(listOfNamesOfNullableFields);
        Objects.requireNonNull(targetObject);
        return Arrays.stream(targetObject.getClass().getDeclaredFields())
                .filter(field -> !listOfNamesOfNullableFields.contains(field.getName()))
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return field.get(targetObject);
                    } catch (IllegalAccessException e) {
                        fail(e);
                        return null;
                    }
                })
                .filter(Objects::isNull)
                .count();
    }

    public static void interruptThreadAfterDelayIfNotAlreadyJoined(
            @NotNull final Thread threadToBeEventuallyInterrupted, int delayInMilliseconds) {
        Executors.newScheduledThreadPool(1)
                .schedule(threadToBeEventuallyInterrupted::stop, delayInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public static String[][] readFromCsvToStringMatrix(@NotNull final String resourceFilePath)
            throws IOException, URISyntaxException {
        URL resource = TestUtility.class.getResource(Objects.requireNonNull(resourceFilePath));
        Path resourcePath = Paths.get(Objects.requireNonNull(resource).toURI());
        List<String> lines = Files.readAllLines(resourcePath)
                .stream().sequential()
                .filter(aLine -> {
                    String trimmedLine = aLine.trim();
                    return isNonEmptyString.test(trimmedLine)
                            && trimmedLine.charAt(0) != CSV_COMMENTED_LINE_INDICATOR;    // avoid commented lines in CSV file
                })
                .collect(Collectors.toList());

        return lines.stream().sequential()
                .map(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR))
                        .map(String::trim)
                        .map(trimmedCell -> {
                            if (trimmedCell.length() >= 2) {
                                char firstCharacter = trimmedCell.charAt(0);
                                char lastCharacter = trimmedCell.charAt(trimmedCell.length() - 1);
                                if (firstCharacter == '"' && lastCharacter == firstCharacter) {
                                    trimmedCell = trimmedCell.substring(1, trimmedCell.length() - 1);
                                }
                            }
                            return trimmedCell;
                        })
                        .toArray(String[]::new))
                .toArray(String[][]::new);
    }
}