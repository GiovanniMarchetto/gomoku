package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GomokuCellFirstCellTest {

    private GomokuCell gomokuCell;

    @BeforeEach
    void setUp(){
        double radius = 10;
        int boardSize = 19;
        gomokuCell = new GomokuCell(new MainViewmodel(), new Coordinates(0,0), radius, boardSize);
    }

    @Test
    void getGroupSize() {
        assertEquals(4, gomokuCell.getGroup().getChildren().size());
    }

    @Test
    void getGroupFirstChildTypes() {
        assertEquals(Line.class, gomokuCell.getGroup().getChildren().get(0).getClass());
    }

    @Test
    void getGroupSecondChildTypes() {
        assertEquals(Line.class, gomokuCell.getGroup().getChildren().get(1).getClass());
    }

    @Test
    void getGroupThirdChildTypes() {
        assertEquals(Circle.class, gomokuCell.getGroup().getChildren().get(2).getClass());
    }

    @Test
    void getGroupFourthChildTypes() {
        assertEquals(Rectangle.class, gomokuCell.getGroup().getChildren().get(3).getClass());
    }
}
