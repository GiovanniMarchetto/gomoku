package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ProxyObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.SettableObservableProperty;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class GomokuGridManager implements Observable {


    @NotNull
    private final SettableObservableProperty<Double> gomokuStoneRadiusProperty;

    //TODO : add nullable/notnull annotations

    private final GUIMainViewmodel vm;
    private final int boardSize;
    private final GridPane gridPane;
    private final Pane parentPane;
    private final double discardWidth;
    private final double discardHeight;

    public GomokuGridManager(GUIMainViewmodel vm, Pane parentPane, double discardWidth, double discardHeight) {
        this.vm = vm;
        this.boardSize = vm.getBoardSize();
        this.parentPane = parentPane;
        this.discardWidth = discardWidth;
        this.discardHeight = discardHeight;
        gomokuStoneRadiusProperty = new SettableObservableProperty<>();
        gridPane = new GridPane();

        parentPane.heightProperty().addListener(onPaneSizeChange());
        parentPane.widthProperty().addListener(onPaneSizeChange());


        IntStream.range(0, boardSize)
                .forEach(i -> {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setHalignment(HPos.CENTER);
                    cc.setHgrow(Priority.NEVER);
                    gridPane.getColumnConstraints().add(cc);

                    RowConstraints rc = new RowConstraints();
                    rc.setValignment(VPos.CENTER);
                    rc.setVgrow(Priority.NEVER);
                    gridPane.getRowConstraints().add(rc);
                });


        IntStream.range(0, boardSize)
                .forEach(row -> IntStream.range(0, boardSize)
                        .forEach(col -> addCell(row, col)));


        //TODO: move these props? (css or not)
//        gridPane.getStyleClass().add("grid");
        gridPane.setStyle("-fx-background-color: peru;");
        gridPane.setAlignment(Pos.CENTER);
    }

    @NotNull
    private ChangeListener<Number> onPaneSizeChange() {
        return (observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                setRadiusAfterResize();
            }
        };
    }

    private void addCell(int row, int col) {
        GomokuCell gc = new GomokuCell(vm, new Coordinates(row, col), new ProxyObservableProperty<>(gomokuStoneRadiusProperty), boardSize);
        gc.observe(this);
        ObservableList<Node> children = gc.getGroup().getChildren();

        IntStream.range(0, children.size())
                .forEach(i -> gridPane.add(children.get(0), col, row));
    }

    private void setRadiusAfterResize() {
        double spaceInHeightForGrid = parentPane.getHeight() - discardHeight;
        double spaceInWidthForGrid = parentPane.getWidth() - discardWidth;
        double minSideOfTotalSpaceForGrid = Math.min(spaceInHeightForGrid, spaceInWidthForGrid);
        if (minSideOfTotalSpaceForGrid == 0) return;
        double newRadiusValue = minSideOfTotalSpaceForGrid / (boardSize * 2.5);
        gomokuStoneRadiusProperty.setPropertyValueAndFireIfPropertyChange(newRadiusValue);
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}
