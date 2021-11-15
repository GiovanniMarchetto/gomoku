package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.actors.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GomokuGridManagerTest {

    private GUIMainViewmodel vm;

    void setupMainViewmodel() {
        vm = new GUIMainViewmodel();
        CPUPlayer p1 = new CPUPlayer("First");
        CPUPlayer p2 = new CPUPlayer("Second");
        int boardSize = 19, numberOfGames = 3;

        try {
            // TODO : refactor method to test MainViewmodel
            Field matchField = MainViewmodel.class.getDeclaredField("match");
            matchField.setAccessible(true);
            matchField.set(vm, new Match(boardSize, numberOfGames, p1, p2));

            startNewGame();

        } catch (NoSuchFieldException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "Error in setup of the model", e);
        }
    }

    private void startNewGame() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method startNewGameMethod = MainViewmodel.class.getDeclaredMethod("startNewGame");
        startNewGameMethod.setAccessible(true);
        startNewGameMethod.invoke(vm);
    }

    @Test
    void getGridPane() {
        Pane parentPane = new Pane();
        setupMainViewmodel();

        GomokuGridManager gomokuGridManager = new GomokuGridManager(vm, parentPane, 0, 0);
        assertEquals(GridPane.class, gomokuGridManager.getGridPane().getClass());
    }
}