package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import it.units.sdm.gomoku.utils.Utility;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;

public class GomokuCell {

    private final int boardSize;
    @NotNull
    private final GUIMainViewmodel guiMainViewmodel;
    @NotNull
    private final Coordinates coordinates;

    private Cell cell;

    private double radius;
    private Rectangle rectangle;
    private Line lineH;
    private Line lineV;
    private Circle circle;

    private Group group;

    public GomokuCell(@NotNull GUIMainViewmodel guiMainViewmodel, @NotNull Coordinates coordinates,
                      @NotNull final ObservableProperty<Double> stoneRadiusProperty, int boardSize) {
        this.guiMainViewmodel = guiMainViewmodel;
        this.coordinates = coordinates;
        this.boardSize = boardSize;
        this.radius = stoneRadiusProperty.getPropertyValue() == null ? 0 : stoneRadiusProperty.getPropertyValue();
        initializeGroup();
        new PropertyObserver<>(guiMainViewmodel.getLastMoveCoordinatesProperty(), evt -> {
            Coordinates lastCoords = (Coordinates) Objects.requireNonNull(evt.getNewValue());
            if (lastCoords.equals(coordinates)) {
                Platform.runLater(() -> {
                    try {
                        setCell(Objects.requireNonNull(guiMainViewmodel.getCellAtCoordinatesInCurrentBoard(lastCoords)));
                    } catch (CellOutOfBoardException e) {
                        Utility.getLoggerOfClass(getClass())
                                .severe("Previous move refers to invalid coordinates, but this should not be possible");
                        throw new IllegalStateException(e);
                    }
                });
            }
            Coordinates penultimateCoords = (Coordinates) evt.getOldValue();
            if (penultimateCoords != null && penultimateCoords.equals(coordinates)) {
                Platform.runLater(this::resetStrokeToPlacedStone);
            }
        });
        new PropertyObserver<>(stoneRadiusProperty, evt -> setRadius((double) evt.getNewValue()));
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
            circle.setFill(cell.getStone().getColor() == it.units.sdm.gomoku.model.custom_types.Color.BLACK ? Color.BLACK : Color.WHITE);
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
        setCell(new Cell());
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
            if (cell.isEmpty() && event.isPrimaryButtonDown() && userCanPlace()) {
                try {
                    guiMainViewmodel.placeStoneFromUser(coordinates);
                } catch (CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
                    Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Invalid coordinates. This should never happen.", e);
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private boolean userCanPlace() {
        return Boolean.TRUE.equals(guiMainViewmodel.getUserMustPlaceNewStoneProperty().getPropertyValue());
    }
}
