package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.ui.gui.GUIMain;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class GUIStartViewTest {

    // TODO : missing tests

    private GUIStartView guiStartView;
    private StartViewmodel guiStartViewmodel;

    private static void setUpJavaFXRuntime() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await(1, TimeUnit.SECONDS);   // Time to keep JavaFX running? // TODO: investigate on this
    }

    private static void tearDownJavaFXRuntime() throws InterruptedException {
        Platform.exit();
    }

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

//    @Test
//    void startMatchButtonOnMouseClicked() {
//        // TODO: all input fields must be valid and already set in viewmodel and view must change
//    }
//
//    @Test
//    void initialize() {
//        // TODO : assert the correct view is shown with correct input field values and same values saved in Viewmodel
//    }

    @AfterEach
    void tearDown() {
        try {
            tearDownJavaFXRuntime();
        } catch (InterruptedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo, Bar"})
    void setPlayer1Name(String newPlayer1Name) {
        String whateverNameDifferentThanThanInputParam = newPlayer1Name + "_different";
        guiStartViewmodel.setPlayer1Name(whateverNameDifferentThanThanInputParam);
        String oldNameSavedInViewmodel = guiStartViewmodel.getPlayer1Name();
        assert !oldNameSavedInViewmodel.equals(newPlayer1Name);
        try {
            TextField player1NameTextField =
                    (TextField) TestUtility
                            .getFieldAlreadyMadeAccessible(guiStartView.getClass(), "player1NameTextField")
                            .get(guiStartView);
            player1NameTextField.textProperty().set(newPlayer1Name);
            guiStartViewmodel.setPlayer1Name(newPlayer1Name);
            assertEquals(guiStartViewmodel.getPlayer1Name(), newPlayer1Name);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

}