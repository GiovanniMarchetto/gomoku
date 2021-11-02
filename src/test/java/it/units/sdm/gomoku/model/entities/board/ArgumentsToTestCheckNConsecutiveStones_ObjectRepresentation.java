package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.utils.Predicates;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.*;

public class ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation {
    // TODO : refactoring needed

    private final Board.Stone[][] matrix;
    private final Coordinates coordinates;
    private final boolean isGameEndedExpected;

    public ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(@NotNull String[][] matrixOfStonesAsString,
                                                                       @NonNegativeIntegerType int xCoordinateOfLastMove,
                                                                       @NonNegativeIntegerType int yCoordinateOfLastMove,
                                                                       boolean isGameEndedExpected) {
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

                this.coordinates = new Coordinates(xCoordinateOfLastMove, yCoordinateOfLastMove);
                this.isGameEndedExpected = isGameEndedExpected;
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid arguments: " + e.getMessage());
            }

        } else {
            throw new IllegalArgumentException("Given matrix is not square but it should.");
        }
    }

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
                        new String[]{"B", "B", "N", "B", "B"},
                        new String[]{"N", "N", "N", "W", "W"},
                        new String[]{"N", "N", "N", "N", "W"}
                }, 0, 0, false).toJson());

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "W", "W", "W", "B"},
                        new String[]{"N", "B", "N", "N", "W"},
                        new String[]{"B", "W", "B", "B", "B"},
                        new String[]{"N", "N", "N", "B", "W"},
                        new String[]{"N", "N", "N", "N", "B"}
                }, 0, 0, true).toJson());

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "W", "W", "W", "B"},
                        new String[]{"N", "B", "N", "B", "W"},
                        new String[]{"B", "W", "B", "B", "B"},
                        new String[]{"N", "B", "N", "N", "W"},
                        new String[]{"B", "N", "N", "N", "B"}
                }, 0, 0, true).toJson());

        data.add(new ArgumentsToTestCheckNConsecutiveStones_ObjectRepresentation(
                new String[][]{
                        new String[]{"B", "B", "W", "W", "B"},
                        new String[]{"W", "B", "B", "W", "W"},
                        new String[]{"B", "W", "W", "B", "B"},
                        new String[]{"B", "W", "W", "B", "W"},
                        new String[]{"B", "W", "W", "B", "B"}
                }, 2, 2, false).toJson());

        return data;
    }

    @NotNull
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t{\n\t");

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
        sb.append(isGameEndedExpected);

        sb.append("\n\t}");

        return sb.toString();
    }
}