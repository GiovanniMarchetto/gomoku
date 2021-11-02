package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GomokuCellFirstCellTest {

    private final Coordinates coordinatesFirstCell = new Coordinates(0, 0);
    private final double radius = 10;
    private final int boardSize = 19;
    private GomokuCell gomokuCell;

    @BeforeEach
    void setUp() {
        gomokuCell = new GomokuCell(new MainViewmodel(), coordinatesFirstCell, radius, boardSize);
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

    @Test
    void radiusBeforePropertyChange() {
        try {
            Field radiusField = GomokuCell.class.getDeclaredField("radius");
            radiusField.setAccessible(true);
            assertEquals(radius, radiusField.get(gomokuCell));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void radiusPropertyChange() {
        int randomOldValue = 3;
        double expectedNewValue = 5;
        PropertyChangeEvent evt = new PropertyChangeEvent(
                new Object(), "radius", randomOldValue, expectedNewValue);
        gomokuCell.propertyChange(evt);
        try {
            Field radiusField = GomokuCell.class.getDeclaredField("radius");
            radiusField.setAccessible(true);
            assertEquals(expectedNewValue, radiusField.get(gomokuCell));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
