package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.ui.gui.GUIMain;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class GUIStartViewTest {

    // TODO : missing tests

    private GUIStartView guiStartView;
    private StartViewmodel guiStartViewmodel;
    private static final AtomicBoolean isJavaFxRunning = new AtomicBoolean(false);

    @BeforeEach
    void setUp() {
        try {
            setUpJavaFXRuntime();
            Method sceneCreatorMethod = SceneController.class.getDeclaredMethod(
                    "createScene", FXMLLoader.class, double.class, double.class);
            sceneCreatorMethod.setAccessible(true);
            FXMLLoader fxmlLoader = new FXMLLoader(GUIStartView.class.getResource(GUIMain.START_VIEW_FXML_FILE_NAME));
            sceneCreatorMethod.invoke(null, fxmlLoader, 0, 0);
            guiStartView = fxmlLoader.getController();
            guiStartViewmodel = guiStartView.getViewmodelAssociatedWithView();
        } catch (InterruptedException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail(e);
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            tearDownJavaFXRuntime();
        } catch (InterruptedException e) {
            fail(e);
        }
    }

//    @Test
//    void startMatchButtonOnMouseClicked() {
//        // TODO: all input fields must be valid and already set in viewmodel and view must change
//    }
//
//    @Test
//    void initialize() {
//        // TODO : assert the correct view is shown with correct input field values and same values saved in Viewmodel
//    }

    private static void setUpJavaFXRuntime() throws InterruptedException {
        if (!isJavaFxRunning.get()) {
            isJavaFxRunning.set(true);
            Platform.startup(() -> {
                while (isJavaFxRunning.get()) {
                }
            });
        }
    }

    private static void tearDownJavaFXRuntime() throws InterruptedException {
        isJavaFxRunning.set(false);
        Platform.setImplicitExit(false);
    }

    @ParameterizedTest
    @CsvSource({
            "One, One, player1NameTextField, player1Name",
            "One, Two, player1NameTextField, player1Name",
            "Two, One, player2NameTextField, player2Name",
            "Two, Two, player2NameTextField, player2Name"
    })
    void updatePlayerNameInViewShouldAutomaticallyUpdateFieldInViewmodel(
            String oldPlayerName, String newPlayerName, String textfieldNameInView, String fieldNameInViewmodel) {
        try {
            TextField playerNameTextField =
                    (TextField) TestUtility
                            .getFieldAlreadyMadeAccessible(guiStartView.getClass(), textfieldNameInView)
                            .get(guiStartView);
            assertSynchronizationBetweenViewAndViewmodel(
                    oldPlayerName, newPlayerName, fieldNameInViewmodel,
                    propertyValue -> playerNameTextField.textProperty().set(propertyValue));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "true, true, player1CPUCheckBox, player1CPU",
            "true, false, player1CPUCheckBox, player1CPU",
            "false, true, player1CPUCheckBox, player1CPU",
            "false, false, player1CPUCheckBox, player1CPU"
    })
    void updatePlayerIsCPUCheckboxInViewShouldAutomaticallyUpdateFieldInViewmodel(
            boolean wasCPUBeforeUpdate, boolean isCPUAfterUpdate,
            String checkboxNameInView, String fieldNameInViewmodel) {
        try {
            CheckBox isCPUSelectedCheckBox =
                    (CheckBox) TestUtility
                            .getFieldAlreadyMadeAccessible(guiStartView.getClass(), checkboxNameInView)
                            .get(guiStartView);
            assertSynchronizationBetweenViewAndViewmodel(
                    wasCPUBeforeUpdate, isCPUAfterUpdate, fieldNameInViewmodel, isCPUSelectedCheckBox::setSelected);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    private <T> void assertSynchronizationBetweenViewAndViewmodel(
            @Nullable final T oldValue, @Nullable final T newValue,
            @NotNull final String fieldNameInViewmodel, @NotNull final Consumer<T> propertyValueSetterInView)
            throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue(Objects.requireNonNull(fieldNameInViewmodel), oldValue, Objects.requireNonNull(guiStartViewmodel));
        Objects.requireNonNull(propertyValueSetterInView).accept(oldValue); // set old state before firing property change
        propertyValueSetterInView.accept(newValue);
        assertEquals(TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel), newValue);
    }

}