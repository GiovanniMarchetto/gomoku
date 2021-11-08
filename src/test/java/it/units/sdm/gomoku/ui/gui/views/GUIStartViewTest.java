package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.ui.gui.GUIMain;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

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
            Method sceneCreatorMethod = SceneController.class.getDeclaredMethod("createScene", FXMLLoader.class, double.class, double.class);
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
    @CsvSource({"Foo, player1NameTextField, player1Name", "Foo, player2NameTextField, player2Name"})
    void updatePlayerNameInViewShouldAutomaticallyUpdateFieldInViewmodel(
            String newPlayerName, String textfieldNameInView, String fieldNameInViewmodel) {
        final String whateverNameDifferentThanThanInputParam = TestUtility.getStringDifferentFromGivenOne(newPlayerName);
        try {
            TestUtility.setFieldValue(fieldNameInViewmodel, whateverNameDifferentThanThanInputParam, guiStartViewmodel);
            String oldNameSavedInViewmodel = (String) TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel);
            assert !oldNameSavedInViewmodel.equals(newPlayerName);

            TextField playerNameTextField =
                    (TextField) TestUtility
                            .getFieldAlreadyMadeAccessible(guiStartView.getClass(), textfieldNameInView)
                            .get(guiStartView);
            playerNameTextField.textProperty().set(newPlayerName);
//            TestUtility.setFieldValue(fieldNameInViewmodel, newPlayerName, guiStartViewmodel);

            assertEquals(TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel), newPlayerName);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

}