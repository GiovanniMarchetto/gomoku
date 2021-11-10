package it.units.sdm.gomoku;

import it.units.sdm.gomoku.ui.cli.CLIMain;
import it.units.sdm.gomoku.ui.cli.CLISceneController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CLIProgramFluxTest {

    @BeforeAll
    static void ignoreStdOut() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @Test
    void sceneControllerInstantiatedAfterLaunchingTheApplication() {
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

}
