package it.units.sdm.gomoku;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private final static String PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE = "logging.properties";

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream(PATH_TO_LOGGING_PROPERTIES_LOADED_AS_RESOURCE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getLogger(Main.class.getCanonicalName()).severe("Severe log");
        Logger.getLogger(Main.class.getCanonicalName()).warning("Severe log");
        Logger.getLogger(Main.class.getCanonicalName()).info("Info log");
        Logger.getLogger(Main.class.getCanonicalName()).fine("Fine log");

//         // TODO : for cli/gui selection
//        if (Arrays.asList(args).contains("cli-passedFromGradle")) {
//            System.out.println("Starting CLI...");
//            CLIMain.main(args);
//        } else {
//            System.out.println("Starting GUI...");
//            GUIMain.main(args);
//        }
    }
}
