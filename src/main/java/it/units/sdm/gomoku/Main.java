package it.units.sdm.gomoku;

import it.units.sdm.gomoku.ui.cli.CLIMain;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.gui.GUIMain;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.LogManager;

public class Main {
    private final static String PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE = "logging.properties";
    private final static String ARG_TO_START_GUI = "gui";
    private final static String ARG_TO_START_CLI = "cli";

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream(PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Predicate<String> isValidArg = s -> s.equalsIgnoreCase(ARG_TO_START_GUI)
                || s.equalsIgnoreCase(ARG_TO_START_CLI);

        String input = Arrays.stream(args)
                .filter(isValidArg)
                .findAny()
                .orElse("");

        if (input.equals("")) {
            String message = "Do you want to play with GUI or CLI? ";
            System.out.print(message);
            input = IOUtility.checkInputAndGet(isValidArg, System.out, "\n" + message);
        }
        switch (input) {
            case ARG_TO_START_GUI -> GUIMain.main(args);
            case ARG_TO_START_CLI -> CLIMain.main(args);
            default -> throw new IllegalArgumentException();
        }
    }
}
