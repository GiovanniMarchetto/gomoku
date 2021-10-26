package it.units.sdm.gomoku.ui.cli;

public class Utility {

    public static boolean isValidCharInsertedCaseInsensitive(char insertedByTheUser, char validChar) {
        return Character.toLowerCase(insertedByTheUser)==Character.toLowerCase(validChar);
    }

}
