package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GomokuCellFirstCellTest {

    private final Coordinates coordinatesFirstCell = new Coordinates(0, 0);
    private final double radius = 10;
    private GomokuCell gomokuCell;

    @BeforeEach
    void setUp() {
        var stoneRadiusProperty = new ObservablePropertySettable<>(radius);
        int boardSize = 19;
        gomokuCell = new GomokuCell(new GUIMainViewmodel(), coordinatesFirstCell, stoneRadiusProperty, boardSize);
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
}
