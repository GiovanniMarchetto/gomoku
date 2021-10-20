package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.Observer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.beans.PropertyChangeEvent;

public class GomokuCell implements Observer {

    private final int boardSize;
    private final MainViewmodel vm;
    private final Coordinates coordinates;
    private double radius;

    private Board.Stone stone;

    private Rectangle rectangle;
    private Line lineH;
    private Line lineV;
    private Circle circle;

    private Group group;

    public GomokuCell(MainViewmodel vm, Coordinates coordinates, double initialRadius, int boardSize) {
        this.vm = vm;
        this.coordinates = coordinates;
        this.radius = initialRadius;
        this.boardSize = boardSize;
        initializeGroup();
        vm.addPropertyChangeListener(this);
    }

    private double getRadius() {
        return radius;
    }

    private void setRadius(double value) {
        radius = value;
    }

    private double getRectSide() {
        return getRadius() * 2.5;
    }

    private double getCentralLineLength() {
        return getRectSide() + 1;
    }

    private double getExternalLineLength() {
        return getRectSide() / 2;
    }

    private boolean isExternalRowOrCol(int rowOrCol) {
        return isFirstRowOrCol(rowOrCol) || isLastRowOrCol(rowOrCol);
    }

    private boolean isLastRowOrCol(int rowOrCol) {
        return rowOrCol == boardSize - 1;
    }

    private boolean isFirstRowOrCol(int rowOrCol) {
        return rowOrCol == 0;
    }


    public Group getGroup() {
        return group;
    }

    private void setStone(Board.Stone stone) {
        this.stone = stone;
        if (!stone.isNone()) {
            circle.setOpacity(1);
            circle.setFill(stone == Board.Stone.BLACK ? Color.BLACK : Color.WHITE);
        } else {
            circle.setOpacity(0);
            circle.setFill(Color.AQUAMARINE);
        }
    }


    private void resizeAllItemsOfCell() {
        rectangle.setHeight(getRectSide());
        rectangle.setWidth(getRectSide());
        resizeLineH();
        resizeLineV();
        circle.setRadius(getRadius());
    }

    private void initializeGroup() {
        group = new Group();

        initializeHorizontalLine();
        initializeVerticalLine();

        initializeCircle();

        initializeRectangle();

        initializeEvents();
    }


    private void initializeHorizontalLine() {
        lineH = new Line();
        resizeLineH();
        correctLineHAlignment();
        group.getChildren().add(lineH);
    }

    private void resizeLineH() {
        int col = coordinates.getY();
        lineH.setEndX(getCentralLineLength());
        if (isExternalRowOrCol(col)) {
            lineH.setEndX(getExternalLineLength());
        }
    }

    private void correctLineHAlignment() {
        int col = coordinates.getY();
        if (isFirstRowOrCol(col)) {
            GridPane.setHalignment(lineH, HPos.RIGHT);
        } else if (isLastRowOrCol(col)) {
            GridPane.setHalignment(lineH, HPos.LEFT);
        }
    }

    private void initializeVerticalLine() {
        lineV = new Line();
        resizeLineV();
        correctLineVAlignment();
        group.getChildren().add(lineV);
    }

    private void resizeLineV() {
        int row = coordinates.getX();
        lineV.setEndY(getCentralLineLength());
        if (isExternalRowOrCol(row)) {
            lineV.setEndY(getExternalLineLength());
        }
    }

    private void correctLineVAlignment() {
        int row = coordinates.getX();
        if (isFirstRowOrCol(row)) {
            GridPane.setValignment(lineV, VPos.BOTTOM);
        } else if (isLastRowOrCol(row)) {
            GridPane.setValignment(lineV, VPos.TOP);
        }
    }


    private void initializeCircle() {
        circle = new Circle(getRadius());
        setStone(Board.Stone.NONE);
        group.getChildren().add(circle);
    }


    private void initializeRectangle() {
        rectangle = new Rectangle(getRectSide(), getRectSide());
        rectangle.setOpacity(0);
        rectangle.setStrokeWidth(0);
        group.getChildren().add(rectangle);
    }


    private void initializeEvents() {
        rectangle.setOnMouseEntered(event -> {
            if (stone.isNone()) {
                circle.setOpacity(0.5);
            }
        });
        rectangle.setOnMouseExited(event -> {
            if (stone.isNone()) {
                circle.setOpacity(0);
            }
        });

        rectangle.setOnMousePressed(event -> {
            if (stone.isNone() && event.isPrimaryButtonDown()) {
                vm.placeStone(coordinates);
            }
        });
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String radiusPropertyName = "radius";
        if (evt.getPropertyName().equals(radiusPropertyName)) {
            setRadius((double) evt.getNewValue());
            resizeAllItemsOfCell();
        } else if (evt.getPropertyName().equals(Board.BoardMatrixPropertyName)) {
            Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
            if (cell.getCoordinates().equals(coordinates)) {
                Platform.runLater(() -> setStone(cell.getNewStone()));
            }
        }
    }
}
