package it.units.sdm.gomoku.ui.gui.views;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GomokuGridManagerTest {

    @Test
    void getGridPane() {
        Pane pane = new Pane();
        GomokuGridManager gomokuGridManager = new GomokuGridManager(19,pane,0,0);
        assertEquals(GridPane.class,gomokuGridManager.getGridPane().getClass());
    }
}