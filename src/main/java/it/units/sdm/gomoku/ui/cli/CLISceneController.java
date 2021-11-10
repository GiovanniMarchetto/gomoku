package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;
import it.units.sdm.gomoku.ui.cli.views.CLIStartView;
import it.units.sdm.gomoku.ui.gui.SceneController;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CLISceneController {   // TODO : refactor (common things with GUI)
    // TODO : test

    private static CLISceneController singleInstance;
    private final CLIMainViewmodel cliMainViewmodel;
    private final Map<CLIViewName, Supplier<View<?>>> views;

    private CLISceneController() {
        cliMainViewmodel = new CLIMainViewmodel();
        views = new ConcurrentHashMap<>();
        views.put(CLIViewName.CLI_START_VIEW, () -> new CLIStartView(new StartViewmodel(cliMainViewmodel)));
        views.put(CLIViewName.CLI_MAIN_VIEW, () -> new CLIMainView(cliMainViewmodel));
    }

    public static void initialize() {
        // TODO : very similar to SceneController.initialize()
        if (wasAlreadyInstantiated()) {
            throw new SceneController.SceneControllerNotInstantiatedException(CLISceneController.class.getCanonicalName() + " not instantiated."); // TODO: not too correct to throw an exception of SceneController (another class)
        } else {
            singleInstance = new CLISceneController();
        }
    }

    private static CLISceneController getInstance() {  // TODO : code duplication with SceneController
        if (wasAlreadyInstantiated()) {
            return singleInstance;
        } else {
            throw new SceneController.SceneControllerNotInstantiatedException(CLISceneController.class.getCanonicalName() + " not instantiated."); // TODO: not too correct to throw an exception of SceneController (another class)
        }
    }

    private static boolean wasAlreadyInstantiated() {
        return singleInstance != null;
    }

    public static void passToNewView(@NotNull final CLIViewName viewName) {
        getInstance().getView(Objects.requireNonNull(viewName));
    }

    private void getView(@NotNull final CLIViewName viewName) {
        views.get(Objects.requireNonNull(viewName)).get();
    }

    public enum CLIViewName {CLI_START_VIEW, CLI_MAIN_VIEW}

}
