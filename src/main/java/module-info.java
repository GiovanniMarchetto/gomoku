module it.units.sdm.gomoku {
    requires javafx.controls;
    requires javafx.fxml;


    exports it.units.sdm.gomoku;
    opens it.units.sdm.gomoku to javafx.fxml;
    exports it.units.sdm.gomoku.controllers;
    opens it.units.sdm.gomoku.controllers to javafx.fxml;
    exports it.units.sdm.gomoku.views;
    opens it.units.sdm.gomoku.views to javafx.fxml;
}