package it.units.sdm.gomoku.ui.gui;

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
    private final Map<Views, Supplier<Scene>> scenes;
    private final Stage stage;
    @SafeVarargs
    private SceneController(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                            int sceneWidthInPx, int sceneHeightInPx,
                            int stageMinWidth, int stageMinHeight,
                            @NotNull final Pair<@NotNull Views, @NotNull String>... fxmlFilePaths) {
        this.stage = Objects.requireNonNull(stage);
        this.scenes = Arrays.stream(Objects.requireNonNull(fxmlFilePaths))
                .map(aView -> {
                    Views viewEnum = Objects.requireNonNull(aView.getKey());
                    String fxmlFilePath = Objects.requireNonNull(aView.getValue());
                    return new AbstractMap.SimpleEntry<>(viewEnum, new FXMLLoader(getClass().getResource(fxmlFilePath)));
                })
                .map(aViewEntry -> {
                    Views view = aViewEntry.getKey();
                    FXMLLoader fxmlLoader = aViewEntry.getValue();
                    return new AbstractMap.SimpleEntry<Views, Supplier<Scene>>(view, () -> {
                        try {
                            return new Scene(fxmlLoader.load(), sceneWidthInPx, sceneHeightInPx);
                        } catch (IOException e) {
                            Logger.getLogger(getClass().getCanonicalName())
                                    .severe("I/O Exception in " + getClass().getCanonicalName() + " when creating the scene.");
                            return null;
                        }
                    });
                })
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

        stage.setTitle(Objects.requireNonNull(firstStageTitle));
        stage.setMinWidth(stageMinWidth);
        stage.setMinHeight(stageMinHeight);
        passToScene_(Views.START_VIEW);
        singleInstance = this;
    }

    @SafeVarargs
    public static void initialize(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                                  int initialSceneWidthInPx, int initialSceneHeightInPx,
                                  int stageMinWidth, int stageMinHeight,
                                  @NotNull final Pair<@NotNull Views, @NotNull String>... fxmlFilePaths) {
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

    public static void passToScene(@NotNull final Views viewEnum) {
        getInstance().passToScene_(Objects.requireNonNull(viewEnum));
    }

    private void passToScene_(@NotNull final Views viewEnum) {
        stage.setScene(scenes.get(Objects.requireNonNull(viewEnum)).get());
    }

    public enum Views {
        START_VIEW,
        MAIN_VIEW
    }
}
