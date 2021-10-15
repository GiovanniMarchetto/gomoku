module it.units.sdm.gomoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.logging;
    requires org.json;


    exports it.units.sdm.gomoku;
    opens it.units.sdm.gomoku to javafx.fxml;
    exports it.units.sdm.gomoku.ui.gui.controllers;
    opens it.units.sdm.gomoku.ui.gui.controllers to javafx.fxml;
    exports it.units.sdm.gomoku.ui.gui.views;
    opens it.units.sdm.gomoku.ui.gui.views to javafx.fxml;
}