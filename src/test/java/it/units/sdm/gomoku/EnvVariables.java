package it.units.sdm.gomoku;

import it.units.sdm.gomoku.entities.Board;
import it.units.sdm.gomoku.utils.TestUtility;

public class EnvVariables {
    public final static String INTS_PROVIDER_RESOURCE_LOCATION = "/ints.csv";
    public final static String NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION = "/nonNegativeInts.csv";
    public final static String COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION = "/coupleInts.csv";
    public final static String PLAYERS_NAME_PROVIDER_RESOURCE_LOCATION = "/names.csv";
    public final static String BOARD_19X19_PROVIDER_RESOURCE_LOCATION = "/boardExample19x19.csv";
    public static final Board.Stone[][] boardStone = TestUtility.readBoardStoneFromCSVFile(BOARD_19X19_PROVIDER_RESOURCE_LOCATION);

    public final static String CSV_SEPARATOR = ",";
    public final static String CSV_NEW_LINE = "\n";
    public static final int BOARD_SIZE = 19;
}
