package it.units.sdm.gomoku.ui.cli;

public class Utility {

    public static boolean isY(char insertedByTheUser) {
        return insertedByTheUser=='Y';
    }

    public static boolean isValidCharInserted(char insertedByTheUser, char validChar) {
        return insertedByTheUser==validChar;
    }

}
