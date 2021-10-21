package it.units.sdm.gomoku.ui.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SceneController {

    private static SceneController singleInstance;
    private final Map<ViewName, Supplier<Scene>> scenes;
    private final Stage stage;
    private static Boolean javaFxRunning = null;

    @SafeVarargs
    private SceneController(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                            int sceneWidthInPx, int sceneHeightInPx,
                            int stageMinWidth, int stageMinHeight,
                            @NotNull final Pair<@NotNull ViewName, @NotNull String>... fxmlFilePaths) {
        this.stage = Objects.requireNonNull(stage);
        this.scenes = Arrays.stream(Objects.requireNonNull(fxmlFilePaths))
                .map(pair -> {
                    ViewName name = Objects.requireNonNull(pair.getKey());
                    String fxmlFilePath = Objects.requireNonNull(pair.getValue());
                    return new AbstractMap.SimpleEntry<>(name, getClass().getResource(fxmlFilePath));
                })
                .map(viewEntry -> {
                    ViewName view = viewEntry.getKey();
                    return new AbstractMap.SimpleEntry<ViewName, Supplier<Scene>>(view, () -> {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(viewEntry.getValue());
                            return new Scene(fxmlLoader.load(), sceneWidthInPx, sceneHeightInPx);
                        } catch (IOException e) {
                            Logger.getLogger(getClass().getCanonicalName())
                                    .severe("I/O Exception in " + getClass().getCanonicalName() +
                                            " when creating the scene.\n\t" +
                                            Arrays.stream(e.getStackTrace())
                                                    .map(StackTraceElement::toString)
                                                    .collect(Collectors.joining("\n\t")));
                            return null;
                        }
                    });
                })
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

        stage.setTitle(Objects.requireNonNull(firstStageTitle));
        stage.setMinWidth(stageMinWidth);
        stage.setMinHeight(stageMinHeight);
        passToNewScene_(ViewName.START_VIEW);
        singleInstance = this;
    }

    @SafeVarargs
    public static void initialize(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                                  int initialSceneWidthInPx, int initialSceneHeightInPx,
                                  int stageMinWidth, int stageMinHeight,
                                  @NotNull final Pair<@NotNull ViewName, @NotNull String>... fxmlFilePaths) {
        if (!isJavaFxRunning()) return;
        if (wasAlreadyInstantiated()) {
            throw new IllegalStateException(SceneController.class.getCanonicalName() + " already instantiated.");
        } else {
            new SceneController(
                    Objects.requireNonNull(stage),
                    Objects.requireNonNull(firstStageTitle),
                    initialSceneWidthInPx, initialSceneHeightInPx,
                    stageMinWidth, stageMinHeight,
                    Objects.requireNonNull(fxmlFilePaths)
            );
        }
    }

    private static SceneController getInstance() {
        if (wasAlreadyInstantiated()) {
            return singleInstance;
        } else {
            throw new IllegalStateException(SceneController.class.getCanonicalName() + " not instantiated.");
        }
    }

    private static boolean wasAlreadyInstantiated() {
        return singleInstance != null;
    }

    public static void passToNewScene(@NotNull final SceneController.ViewName viewEnum) {
        if (!isJavaFxRunning()) return;
        getInstance().passToNewScene_(Objects.requireNonNull(viewEnum));
    }

    private void passToNewScene_(@NotNull final SceneController.ViewName viewEnum) {
        executeOnJavaFxUiThread(() -> stage.setScene(scenes.get(Objects.requireNonNull(viewEnum)).get()));
    }

    public static boolean isJavaFxRunning() {
        if (javaFxRunning == null) {
            try {
                Platform.runLater(() -> {});
                javaFxRunning = true;
            } catch (Exception ignored) {
                javaFxRunning = false;
            }
        }
        return javaFxRunning;
    }

    public static void executeOnJavaFxUiThread(Runnable runnable) {
        if (isJavaFxRunning()) {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        } else {
            throw new IllegalCallerException("Cannot invoke this method from a non-JavaFX application! (JavaFX is not running)");
        }
    }

    public enum ViewName {
        START_VIEW,
        MAIN_VIEW,
        SUMMARY_VIEW
    }
}
