package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
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
import java.util.Objects;

public class GomokuCell implements Observer {

    public static final String radiusPropertyName = "radius";

    private final int boardSize;
    private final MainViewmodel vm;
    private final Coordinates coordinates;

    private double radius;

    private Cell cell;

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
        resizeAllItemsOfCell();
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

    private void setCell(Cell cell) {
        this.cell = cell;
        if (cell.isEmpty()) {
            circle.setOpacity(0);
            circle.setFill(Color.AQUAMARINE);
            circle.setStroke(null);
            circle.setStrokeWidth(1.0);
        } else {
            circle.setOpacity(1);
            //noinspection ConstantConditions // already checked
            circle.setFill(cell.getStone().color() == Stone.Color.BLACK ? Color.BLACK : Color.WHITE);
            circle.setStroke(Color.DARKRED);
            circle.setStrokeWidth(3.0);
        }
    }

    private void resetStrokeToPlacedStone() {
        if (cell.isEmpty()) {
            setCell(cell);
        } else {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.0);
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
        cell = new Cell();
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
            if (cell.isEmpty()) {
                circle.setOpacity(0.5);
            }
        });
        rectangle.setOnMouseExited(event -> {
            if (cell.isEmpty()) {
                circle.setOpacity(0);
            }
        });

        rectangle.setOnMousePressed(event -> {
            if (cell.isEmpty() && event.isPrimaryButtonDown()) {
                try {
                    vm.placeStoneFromUser(coordinates);
                } catch (Board.BoardIsFullException |
                        Board.CellAlreadyOccupiedException e) {
                    e.printStackTrace();    // TODO : handle this exception (should never happen)
                    // Possible things to do:
                    // force update stone (in GUI) at current coordinates, or...
//                    setCell(vm.getCellAtCoordinatesInCurrentBoard(coordinates));
                    // ... force update all stones
                    vm.forceReFireAllCells();

                }
            }
        });
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case radiusPropertyName -> setRadius((double) evt.getNewValue());
            case AbstractMainViewmodel.lastMoveCoordinatesPropertyName -> {
                Coordinates lastCoords = (Coordinates) Objects.requireNonNull(evt.getNewValue());
                if(lastCoords.equals(coordinates)) {
                    Platform.runLater(() -> setCell(Objects.requireNonNull(vm.getCellAtCoordinatesInCurrentBoard(lastCoords))));
                }

                Coordinates penultimateCoords = (Coordinates) evt.getOldValue();
                if (penultimateCoords != null && penultimateCoords.equals(coordinates)) {
                    Platform.runLater(this::resetStrokeToPlacedStone);
                }
            }
        }
    }
}
