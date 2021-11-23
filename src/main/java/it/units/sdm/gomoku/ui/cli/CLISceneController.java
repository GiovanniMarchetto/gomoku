package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;
import it.units.sdm.gomoku.ui.cli.views.CLIStartView;
import it.units.sdm.gomoku.ui.cli.views.CLISummaryView;
import it.units.sdm.gomoku.ui.exceptions.SceneControllerNotInstantiatedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CLISceneController {
    @Nullable
    private static CLISceneController singleInstance;
    @Nullable
    private static View<?> currentView;

    @NotNull
    private final AtomicReference<StartViewmodel> startViewmodelAtomicReference;

    @NotNull
    private final CLIMainViewmodel cliMainViewmodel;

    @NotNull
    private final Map<CLIViewName, List<View<?>>> historyOfCreatedViews;

    @NotNull
    private final Map<CLIViewName, Supplier<View<?>>> views;

    private CLISceneController() {
        historyOfCreatedViews = Arrays.stream(CLIViewName.values())
                .map(viewName -> new AbstractMap.SimpleEntry<>(viewName, new ArrayList<View<?>>()))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        cliMainViewmodel = new CLIMainViewmodel();
        startViewmodelAtomicReference = new AtomicReference<>();
        views = new ConcurrentHashMap<>();

        views.put(CLIViewName.CLI_START_VIEW, () -> {
            startViewmodelAtomicReference.set(new StartViewmodel(cliMainViewmodel));
            return addViewToHistoryAndGet(
                    CLIViewName.CLI_START_VIEW, new CLIStartView(startViewmodelAtomicReference.get()));
        });
        views.put(CLIViewName.CLI_MAIN_VIEW, () ->
                addViewToHistoryAndGet(CLIViewName.CLI_MAIN_VIEW, new CLIMainView(cliMainViewmodel)));
        views.put(CLIViewName.CLI_SUMMARY_VIEW, () ->
                addViewToHistoryAndGet(CLIViewName.CLI_SUMMARY_VIEW, new CLISummaryView(cliMainViewmodel)));
    }

    public static void initialize() {
        if (wasAlreadyInstantiated()) {
            throw new SceneControllerNotInstantiatedException(
                    CLISceneController.class.getCanonicalName() + " not instantiated.");
        } else {
            singleInstance = new CLISceneController();
        }
    }

    private static CLISceneController getInstance() {
        if (wasAlreadyInstantiated()) {
            return singleInstance;
        } else {
            throw new SceneControllerNotInstantiatedException(
                    CLISceneController.class.getCanonicalName() + " not instantiated.");
        }
    }

    private static boolean wasAlreadyInstantiated() {
        return singleInstance != null;
    }

    public static void passToNewView(@NotNull final CLIViewName viewName) {
        if (currentView != null) {
            currentView.onViewDisappearing();
        }
        currentView = getInstance().getView(Objects.requireNonNull(viewName));
        currentView.onViewInitialized();
    }

    private View<?> addViewToHistoryAndGet(@NotNull final CLIViewName cliViewName, @NotNull final View<?> newView) {
        historyOfCreatedViews.get(Objects.requireNonNull(cliViewName))
                .add(Objects.requireNonNull(newView));
        return newView;
    }

    private View<?> getView(@NotNull final CLIViewName viewName) {
        return views.get(Objects.requireNonNull(viewName)).get();
    }

    public enum CLIViewName {CLI_START_VIEW, CLI_MAIN_VIEW, CLI_SUMMARY_VIEW}

}
