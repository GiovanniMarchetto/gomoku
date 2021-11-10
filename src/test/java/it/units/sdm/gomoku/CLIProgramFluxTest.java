package it.units.sdm.gomoku;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.CLIMain;
import it.units.sdm.gomoku.ui.cli.CLISceneController;
import it.units.sdm.gomoku.ui.cli.views.CLIStartView;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CLIProgramFluxTest {

    private static final Supplier<CLISceneController> cliSceneControllerInstanceGetter = () -> {
        try {
            Method cliSceneControllerInstanceGetterMethod = CLISceneController.class.getDeclaredMethod("getInstance");
            cliSceneControllerInstanceGetterMethod.setAccessible(true);
            return (CLISceneController) cliSceneControllerInstanceGetterMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail(e);
            return null;
        }
    };

    @BeforeAll
    static void ignoreStdOut() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @Test
    void launchApplicationAndCheckSceneControllerInstantiation() {
        try {
            Method launcher = CLIMain.class.getDeclaredMethod("launch");
            launcher.setAccessible(true);
            Method wasSceneControllerAlreadyInstantiatedMethod = CLISceneController.class.getDeclaredMethod("wasAlreadyInstantiated");
            wasSceneControllerAlreadyInstantiatedMethod.setAccessible(true);
            try {
                launcher.invoke(null);
            } catch (Exception ignored) {
            }
            assertTrue((boolean) wasSceneControllerAlreadyInstantiatedMethod.invoke(null));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void checkFirstView() {
        launchApplicationAndCheckSceneControllerInstantiation();
        View<?> actualCurrentView = null;
        try {
            actualCurrentView = (View<?>) TestUtility.getFieldValue("currentView", cliSceneControllerInstanceGetter.get());
            assertTrue(actualCurrentView instanceof CLIStartView);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

}