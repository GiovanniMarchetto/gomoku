package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GomokuGridManagerTest {

    private MainViewmodel vm;

    void setupMainViewmodel() {
        vm = new MainViewmodel();
        CPUPlayer p1 = new CPUPlayer("First");
        CPUPlayer p2 = new CPUPlayer("Second");
        int boardSize = 19, numberOfGames = 3;

        try {
            Field matchField = vm.getClass().getDeclaredField("match");
            matchField.setAccessible(true);
            matchField.set(vm, new Match(p1, p2, boardSize, numberOfGames));

            vm.startNewGame();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void getGridPane() {
        Pane parentPane = new Pane();
        setupMainViewmodel();

        GomokuGridManager gomokuGridManager = new GomokuGridManager(vm, parentPane, 0, 0);
        assertEquals(GridPane.class, gomokuGridManager.getGridPane().getClass());
    }
}