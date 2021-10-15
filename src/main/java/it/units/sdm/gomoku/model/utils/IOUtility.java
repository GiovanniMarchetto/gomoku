package it.units.sdm.gomoku.model.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.units.sdm.gomoku.model.utils.Predicates.isNonEmptyString;

public class IOUtility {

    public final static String CSV_SEPARATOR = ",";
    public final static String CSV_NEW_LINE = "\n";

    private final static char CSV_COMMENTED_LINE_INDICATOR = '#';

    public static String[][] readFromCsvToStringMatrix(@NotNull final String resourceFilePath)
            throws IOException, URISyntaxException {
        URL resource = IOUtility.class.getResource(Objects.requireNonNull(resourceFilePath));
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
