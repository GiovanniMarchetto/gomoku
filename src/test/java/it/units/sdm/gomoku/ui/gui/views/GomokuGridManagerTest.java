package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.cli.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
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

    private MainViewmodel vm;

    void setupMainViewmodel() {
        vm = new MainViewmodel();
        CPUPlayer p1 = new CPUPlayer("First");
        CPUPlayer p2 = new CPUPlayer("Second");
        int boardSize = 19, numberOfGames = 3;

        try {
            // TODO : refactor method to test AbstractMainViewmodel
            Field matchField = AbstractMainViewmodel.class.getDeclaredField("match");
            matchField.setAccessible(true);
            matchField.set(vm, new Match(p1, p2, boardSize, numberOfGames));

            startNewGame();

        } catch (NoSuchFieldException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "Error in setup of the model", e);
        }
    }

    private void startNewGame() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method startNewGameMethod = AbstractMainViewmodel.class.getDeclaredMethod("startNewGame");
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