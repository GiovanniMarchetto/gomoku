package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.mvvm_library.Observer;
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

    public static final String radiusPropertyName = "radius";

    private final int boardSize;
    private final MainViewmodel vm;
    private final Coordinates coordinates;

    private double radius;

    private Stone stone;

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

    private void setStone(Stone stone) {
        this.stone = stone;
        if (!stone.isNone()) {
            circle.setOpacity(1);
            circle.setFill(stone == Stone.BLACK ? Color.BLACK : Color.WHITE);
            circle.setStroke(Color.DARKRED);
            circle.setStrokeWidth(3.0);
        } else {
            circle.setOpacity(0);
            circle.setFill(Color.AQUAMARINE);
            circle.setStroke(null);
            circle.setStrokeWidth(1.0);
        }
    }

    private void resetStrokeToPlacedStone() {
        if (!stone.isNone()) {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.0);
        } else {
            setStone(stone);
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
        setStone(Stone.NONE);
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
                try {
                    vm.placeStoneFromUser(coordinates);
                } catch (Board.NoMoreEmptyPositionAvailableException |
                        Board.PositionAlreadyOccupiedException e) {
                    e.printStackTrace();    // TODO : handle this exception (should never happen)
                    // Possible things to do:
                    // force update stone (in GUI) at current coordinates, or...
//                    setStone(vm.getStoneAtCoordinatesInCurrentBoard(coordinates));
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
            case Board.boardMatrixPropertyName -> {
                if (evt.getNewValue() != null) {
                    Board board = (Board) evt.getNewValue();
                    int nOfStones = board.getCoordinatesHistory().size();

                    Coordinates lastCoords = board.getCoordinatesHistory().get(nOfStones - 1);
                    if (lastCoords.equals(coordinates)) {
                        Platform.runLater(() -> setStone(board.getStoneAtCoordinates(lastCoords)));
                    }

                    Coordinates penultimateCoords = null;
                    if (board.getCoordinatesHistory().size() > 1) {
                        penultimateCoords = board.getCoordinatesHistory().get(nOfStones - 2);
                    }
                    if (penultimateCoords != null && penultimateCoords.equals(coordinates)) {
                        Platform.runLater(this::resetStrokeToPlacedStone);
                    }
                } else {
                    Platform.runLater(() -> {
                        setStone(vm.getStoneAtCoordinatesInCurrentBoard(coordinates));
                        resetStrokeToPlacedStone();
                    });
                }
            }
        }
    }
}
