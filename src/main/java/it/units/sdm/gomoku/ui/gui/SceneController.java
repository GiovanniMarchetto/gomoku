package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.mvvm_library.View;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
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

public class SceneController {  // todo : TEST

    private static SceneController singleInstance;
    private static Boolean javaFxRunning = null;
    private final Map<ViewName, Supplier<Scene>> scenes;
    private final Stage stage;
    private double sceneWidth = 0;
    private double sceneHeight = 0;

    @SafeVarargs
    private SceneController(@NotNull final Stage stage, @NotNull final String firstStageTitle,  // TODO: should pass a setup objects instead of so many parameters?
                            double initialSceneWidth, double initialSceneHeight,
                            double stageMinWidth, double stageMinHeight,
                            @NotNull final Pair<@NotNull ViewName, @NotNull String>... fxmlFilePaths) {
        this.stage = Objects.requireNonNull(stage);
        this.scenes = Arrays.stream(Objects.requireNonNull(fxmlFilePaths))
                .map(pair -> {
                    ViewName name = Objects.requireNonNull(pair.getKey());
                    String fxmlFilePath = Objects.requireNonNull(pair.getValue());
                    return new AbstractMap.SimpleEntry<>(name, getClass().getResource(fxmlFilePath));
                })
                .map(viewEntry -> {
                    ViewName viewName = viewEntry.getKey();
                    return new AbstractMap.SimpleEntry<ViewName, Supplier<Scene>>(viewName, () -> {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(viewEntry.getValue());
                            sceneWidth = sceneWidth > 0 ? stage.getScene().getWidth() : initialSceneWidth;
                            sceneHeight = sceneHeight > 0 ? stage.getScene().getHeight() : initialSceneHeight;
                            return createScene(fxmlLoader, sceneWidth, sceneHeight);
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
        passToNewScene(ViewName.START_VIEW);
    }

    @NotNull
    private static Scene createScene(@NotNull final FXMLLoader fxmlLoader, double sceneWidth, double sceneHeight) throws IOException {
        StackPane parentPane = new StackPane();
        parentPane.getChildren().add(fxmlLoader.load());
        var scene = new Scene(Objects.requireNonNull(parentPane), sceneWidth, sceneHeight);
        if (fxmlLoader.getController() instanceof View view) {
            view.onViewInitialized();
        }
        return scene;
    }

    @SafeVarargs
    public static void initialize(@NotNull final Stage stage, @NotNull final String firstStageTitle,
                                  int initialSceneWidthInPx, int initialSceneHeightInPx,
                                  int stageMinWidth, int stageMinHeight,
                                  @NotNull final Pair<@NotNull ViewName, @NotNull String>... fxmlFilePaths) {
        if (!isJavaFxRunning()) return;
        if (wasAlreadyInstantiated()) {
            throw new SceneControllerAlreadyInstantiatedException(SceneController.class.getCanonicalName() + " already instantiated.");
        } else {
            singleInstance = new SceneController(
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
            throw new SceneControllerNotInstantiatedException(SceneController.class.getCanonicalName() + " not instantiated.");
        }
    }

    private static boolean wasAlreadyInstantiated() {
        return singleInstance != null;
    }

    public static void passToNewSceneIfIsGUIRunningOrDoNothing(@NotNull final SceneController.ViewName viewEnum) {
        if (!isJavaFxRunning()) return;
        getInstance().passToNewScene(Objects.requireNonNull(viewEnum));
    }

    public static void fadeOutSceneIfIsGUIRunningOrDoNothing(@NotNull final SceneController.ViewName viewEnum, final int fadeDurationMillis) {
        if (!isJavaFxRunning()) return;
        getInstance().fadeToNewScene(Objects.requireNonNull(viewEnum), fadeDurationMillis);
    }

    public static boolean isJavaFxRunning() {
        if (javaFxRunning == null) {
            try {
                Platform.runLater(() -> {
                });
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

    private void passToNewScene(@NotNull final SceneController.ViewName viewEnum) {
        executeOnJavaFxUiThread(() -> stage.setScene(scenes.get(Objects.requireNonNull(viewEnum)).get()));
    }

    private void fadeToNewScene(@NotNull final SceneController.ViewName viewEnum, int fadeDurationMillis) {
        Scene oldScene = stage.getScene();
        if (oldScene != null) {
            executeOnJavaFxUiThread(() -> {

                StackPane oldRoot = (StackPane) oldScene.getRoot();
                ObservableList<Node> children = oldRoot.getChildren();
                Node lastChildOfOldRoot = children.get(children.size() - 1);

                Scene newScene = scenes.get(Objects.requireNonNull(viewEnum)).get();
                StackPane newRoot = (StackPane) newScene.getRoot();
                Node firstChildOfNewRoot = newRoot.getChildren().remove(0);

                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(fadeDurationMillis), firstChildOfNewRoot);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();

                newRoot.getChildren().add(0, lastChildOfOldRoot);
                newRoot.getChildren().add(1, firstChildOfNewRoot);

                stage.setScene(newScene);
            });
        } else {
            passToNewScene(viewEnum);
        }
    }

    public enum ViewName {START_VIEW, MAIN_VIEW, SUMMARY_VIEW}

    public static class SceneControllerNotInstantiatedException extends IllegalStateException {
        //   TODO : test ?
        public SceneControllerNotInstantiatedException(@NotNull final String errorMessage) {
            super(Objects.requireNonNull(errorMessage));
        }
    }

    public static class SceneControllerAlreadyInstantiatedException extends IllegalStateException { // TODO: needed?
        //   TODO : test ?
        public SceneControllerAlreadyInstantiatedException(@NotNull final String errorMessage) {
            super(Objects.requireNonNull(errorMessage));
        }
    }
}
