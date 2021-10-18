package it.units.sdm.gomoku.ui.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class SceneController {
    private static SceneController singleInstance;

    private final Supplier<?>[] sceneArray;
    private final Stage stage;
    private int currentSceneIndex;

    private SceneController(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                            int sceneWidthInPx, int sceneHeightInPx,
                            int stageMinWidth, int stageMinHeight,
                            @NotNull @Range(from = 0, to = Integer.MAX_VALUE) final String... fxmlFilePaths) {
        this.stage = Objects.requireNonNull(stage);
        this.sceneArray = Arrays.stream(Objects.requireNonNull(fxmlFilePaths)).sequential()
                .map(fxmlFilePath -> new FXMLLoader(getClass().getResource(fxmlFilePath)))
                .map(fxmlLoader -> (Supplier<Scene>)(() -> {
                    try {
                        return new Scene(fxmlLoader.load(), sceneWidthInPx, sceneHeightInPx);
                    } catch (IOException e) {
                        Logger.getLogger(getClass().getCanonicalName())
                                .severe("I/O Exception in " + getClass().getCanonicalName() + " when creating the scene.");
                        return null;
                    }
                }))
                .toArray(Supplier<?>[]::new);
        stage.setTitle(Objects.requireNonNull(firstStageTitle));
        stage.setMinWidth(stageMinWidth);
        stage.setMinHeight(stageMinHeight);
        this.currentSceneIndex = 0;
        passToNextScene_();
        singleInstance = this;
    }

    public static void initialize(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                                  int initialSceneWidthInPx, int initialSceneHeightInPx,
                                  int stageMinWidth, int stageMinHeight,
                                  @NotNull @Range(from = 0, to = Integer.MAX_VALUE) final String... fxmlFilePaths) {
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

    public static void passToNextScene() {
        getInstance().passToNextScene_();
    }

    private void passToNextScene_() {
        if (currentSceneIndex < sceneArray.length) {
            stage.setScene((Scene) sceneArray[currentSceneIndex++].get());
        } else {
            throw new IndexOutOfBoundsException("No more scenes available.");
        }
    }
}
