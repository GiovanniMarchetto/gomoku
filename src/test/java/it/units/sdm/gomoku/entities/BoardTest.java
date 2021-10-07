package it.units.sdm.gomoku.entities;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static final int BOARD_SIZE = 19;
    final static String RESOURCE_PATH = "/boardExample"+BOARD_SIZE+"x"+BOARD_SIZE+".csv";

    private static final Board.Stone[][] boardStone = readBoardStoneFromCSVFile(RESOURCE_PATH);

    static Board.Stone[][] readBoardStoneFromCSVFile(@NotNull String filePath) {
        final String CSV_SEPARATOR = ",";
        try {
            List<String> lines = Files.readAllLines( Paths.get(Objects.requireNonNull(BoardTest.class.getResource(RESOURCE_PATH)).toURI()) )
                    .stream().sequential()
                    .filter(aLine -> aLine.trim().charAt(0)!='#')   // avoid commented lines in CSV file
                    .collect(Collectors.toList());

            return lines.stream().sequential()
                    .map( aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR))
                            .map(Board.Stone::valueOf)
                            .toArray(Board.Stone[]::new))
                    .toArray(Board.Stone[][]::new);

        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Board.Stone[0][0];
        }
    }

}