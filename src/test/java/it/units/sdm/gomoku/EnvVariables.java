package it.units.sdm.gomoku;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;

public class EnvVariables {
    public final static String INTS_PROVIDER_RESOURCE_LOCATION = "/ints.csv";
    public final static String NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION = "/nonNegativeInts.csv";
    public final static String COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION = "/coupleInts.csv";
    public final static String PLAYERS_NAME_PROVIDER_RESOURCE_LOCATION = "/names.csv";
    public final static String BOARD_19X19_PROVIDER_RESOURCE_LOCATION = "/boardExample19x19.csv";
    public final static String CSV_SAMPLE_FILE_2X2_INT_MATRIX_PROVIDER_RESOURCE_LOCATION = "/csvSampleFile2x2Ints.csv";
    public final static String CSV_SAMPLE_FILE_2X2_STRING_MATRIX_PROVIDER_RESOURCE_LOCATION = "/csvSampleFile2x2Strings.csv";
    public final static String END_GAMES = "/endGames.json";
    public final static String COORDINATES_PROVIDER_RESOURCE_LOCATION = "/coordinatesFor19x19.csv";

    public static final PositiveInteger BOARD_SIZE = new PositiveInteger(19);
    public static final int INT_NUMBER_REPETITIONS_TEST = 40;
}