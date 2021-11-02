package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GomokuCellTest {

    private final double radius = 10;
    private final int boardSize = 19;
    private GomokuCell gomokuCell;

    void setUpGomokuCell(int x, int y) {
        gomokuCell = new GomokuCell(new MainViewmodel(), new Coordinates(x, y), radius, boardSize);
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
    void getLineHLength(int x, int y) {
        setUpGomokuCell(x, y);
        Line line = (Line) gomokuCell.getGroup().getChildren().get(0);
        double actualLength = line.getEndX() - line.getStartX();

        assertEquals(getExpectedLength(y), actualLength);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COORDINATES_PROVIDER_RESOURCE_LOCATION)
    void getLineVLength(int x, int y) {
        setUpGomokuCell(x, y);
        Line line = (Line) gomokuCell.getGroup().getChildren().get(1);
        double actualLength = line.getEndY() - line.getStartY();

        assertEquals(getExpectedLength(x), actualLength);
    }

    private double getExpectedLength(int axis) {
        double rectSideLength = radius * 2.5;
        double lineExtLength = rectSideLength / 2;
        double lineIntLength = rectSideLength + 1;

        return (axis == 0 || axis == boardSize - 1)
                ? lineExtLength
                : lineIntLength;
    }

    @Test
    void propertyChange() {
        //TODO: to do
    }
}