package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GomokuCellTest {

    private final double radius = 10;
    private final int boardSize = 19;
    private final double rectSideLength = radius * 2.5;
    private final double lineIntLength = rectSideLength + 1;
    private final double lineExtLength = rectSideLength / 2;
    private GomokuCell gomokuCell;

    void setUpGomokuCell(int x, int y) {
        gomokuCell = new GomokuCell(new MainViewmodel(), new Coordinates(x, y), radius, boardSize);
    }

    @Test
    void getGroupSize() {
        setUpGomokuCell(0, 0);
        assertEquals(4, gomokuCell.getGroup().getChildren().size());
    }

    @Test
    void getGroupFirstChildTypes() {
        setUpGomokuCell(0, 0);
        assertEquals(Line.class, gomokuCell.getGroup().getChildren().get(0).getClass());
    }

    @Test
    void getGroupSecondChildTypes() {
        setUpGomokuCell(0, 0);
        assertEquals(Line.class, gomokuCell.getGroup().getChildren().get(1).getClass());
    }

    @Test
    void getGroupThirdChildTypes() {
        setUpGomokuCell(0, 0);
        assertEquals(Circle.class, gomokuCell.getGroup().getChildren().get(2).getClass());
    }

    @Test
    void getGroupFourthChildTypes() {
        setUpGomokuCell(0, 0);
        assertEquals(Rectangle.class, gomokuCell.getGroup().getChildren().get(3).getClass());
    }


    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COORDINATES_PROVIDER_RESOURCE_LOCATION)
    void getLinesStart(int x, int y) {
        setUpGomokuCell(x, y);
        Line lineH = (Line) gomokuCell.getGroup().getChildren().get(0);
        Line lineV = (Line) gomokuCell.getGroup().getChildren().get(1);

        assertEquals(0, lineH.getStartX());
        assertEquals(0, lineH.getStartY());

        assertEquals(0, lineV.getStartX());
        assertEquals(0, lineV.getStartY());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COORDINATES_PROVIDER_RESOURCE_LOCATION)
    void getLinesLength(int x, int y) {
        setUpGomokuCell(x, y);
        IntStream.range(0, 2).unordered().parallel().forEach(i -> {
                    Line line = (Line) gomokuCell.getGroup().getChildren().get(i);
                    int axis = i == 0 ? y : x;
                    double expectedLength = (axis == 0 || axis == boardSize - 1)
                            ? lineExtLength
                            : lineIntLength;
                    double actualLength = (i == 0)
                            ? line.getEndX() - line.getStartX()
                            : line.getEndY() - line.getStartY();
                    assertEquals(expectedLength, actualLength);
                }
        );
    }


    @Test
    void propertyChange() {
        //TODO: to do
    }
}