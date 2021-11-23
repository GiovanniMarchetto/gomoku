package it.units.sdm.gomoku;

import it.units.sdm.gomoku.ui.cli.CLIMain;
import it.units.sdm.gomoku.ui.gui.GUIMain;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.LogManager;

public class Main {
    private final static String PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE = "logging.properties";

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream(PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Arrays.asList(args).contains("cli-passedFromGradle")) {
            System.out.println("Starting CLI...");
            CLIMain.main(args);
        } else {
            System.out.println("Starting GUI...");
            GUIMain.main(args);
        }
    }
}
