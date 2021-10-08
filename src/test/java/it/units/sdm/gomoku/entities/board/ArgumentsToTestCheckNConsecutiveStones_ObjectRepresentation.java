package it.units.sdm.gomoku.entities.board;

import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.entities.Board;
import it.units.sdm.gomoku.utils.Predicates;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation {
    // TODO : refactoring needed

    public static void main(String[] args) {
        // Used to create JSON representation of instances of the class to use in tests

        List<String> data = dataProvider();

        String s = "{\n\t\"data\": [\n\t\t" +
                String.join(",\n", data).replaceAll("\n", "\n\t") +
                "\n\t]\n}";

        System.out.println(s);
    }

    @NotNull
    private static List<String> dataProvider() {

        // TODO : find an effective way to test all possible combinations

        List<String> data = new ArrayList<>();

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "B", "B", "W", "W"},
                        new String[]{"N", "N", "N", "N", "W"},
                        new String[]{"B", "B", "B", "W", "W"},
                        new String[]{"N", "N", "N", "B", "W"},
                        new String[]{"N", "N", "N", "N", "W"}
                }, 4, 4, true).toJson());

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "W", "W", "W", "B"},
                        new String[]{"N", "N", "N", "N", "W"},
                        new String[]{"B", "B", "B", "B", "B"},
                        new String[]{"N", "N", "N", "W", "W"},
                        new String[]{"N", "N", "N", "N", "W"}
                }, 2, 4, true).toJson());

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "W", "W", "W", "B"},
                        new String[]{"N", "N", "N", "N", "W"},
                        new String[]{"B", "B", "B", "B", "B"},
                        new String[]{"N", "N", "N", "W", "W"},
                        new String[]{"N", "N", "N", "N", "W"}
                }, 0, 0, false).toJson());

        return data;
    }


    @SuppressWarnings("FieldCanBeLocal")    // used for JSON de/serialization
    private Board.Stone[][] matrix = null;
    @SuppressWarnings("FieldCanBeLocal")
    private Coordinates coordinates = null;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean expected = false;

    private ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation() {
    }

    public ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(@NotNull Board.Stone[][] matrix, @NotNull Coordinates coordinates, boolean expected) {
        if (Predicates.isSquareMatrixOfGivenSize.test(matrix, matrix.length)) {
            this.matrix = matrix;
            this.coordinates = coordinates;
            this.expected = expected;
        } else {
            throw new IllegalArgumentException("Given matrix is not square but it should.");
        }
    }

    public ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(@NotNull String[][] matrixOfStonesAsString,
                                                                       @NonNegativeInteger.NonNegativeIntegerType int cordX,
                                                                       @NonNegativeInteger.NonNegativeIntegerType int cordY,
                                                                       boolean expected) {
        if (Predicates.isSquareMatrixOfGivenSize.test(matrixOfStonesAsString, matrixOfStonesAsString.length)) {

            Map<Character, Board.Stone> correspondence_firstChar_Stone = Arrays.stream(Board.Stone.values())
                    .map(enumValue -> new AbstractMap.SimpleEntry<>(enumValue.toString().charAt(0), enumValue))
                    .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

            try {
                this.matrix = Arrays.stream(matrixOfStonesAsString)
                        .map(aRow -> Arrays.stream(aRow)
                                .map(aCell -> correspondence_firstChar_Stone.get(aCell.charAt(0)))
                                .toArray(Board.Stone[]::new)
                        )
                        .toArray(Board.Stone[][]::new);

                this.coordinates = new Coordinates(cordX, cordY);
                this.expected = expected;
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid arguments: " + e.getMessage());
            }

        } else {
            throw new IllegalArgumentException("Given matrix is not square but it should.");
        }
    }

    @NotNull
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t");

        sb.append("\t\"matrix\": [\n\t\t\t");
        if (matrix != null) {
            sb.append(
                    Arrays.stream(matrix).map(aRow -> "[" + (aRow != null ? Arrays.stream(aRow).map(aCell -> "\"" + aCell + "\"").collect(Collectors.joining(",")) : "") + "]")
                            .collect(Collectors.joining(",\n\t\t\t"))
            );
        }
        sb.append("\n\t\t],\n");

        sb.append("\t\t\"coordinates\": [");
        if (coordinates != null) {
            sb.append(coordinates.getX());
            sb.append(",");
            sb.append(coordinates.getY());
        }
        sb.append("],\n");

        sb.append("\t\t\"expected\": ");
        sb.append(expected);

        sb.append("\n\t}");

        return sb.toString();
    }
}