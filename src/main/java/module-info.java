module it.units.sdm.gomoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.logging;
    requires org.json;
    requires java.desktop;


    exports it.units.sdm.gomoku;
    opens it.units.sdm.gomoku to javafx.fxml;
    exports it.units.sdm.gomoku.ui.gui.views;
    opens it.units.sdm.gomoku.ui.gui.views to javafx.fxml;
    exports it.units.sdm.gomoku.ui.cli;
    exports it.units.sdm.gomoku.model.utils;
    exports it.units.sdm.gomoku.model.entities;
    exports it.units.sdm.gomoku.model.custom_types;
    exports it.units.sdm.gomoku.ui.support;
    opens it.units.sdm.gomoku.ui.support to javafx.fxml;
    exports it.units.sdm.gomoku.ui.cli.io;
    exports it.units.sdm.gomoku.mvvm_library.views;
    opens it.units.sdm.gomoku.mvvm_library.views to javafx.fxml;
    exports it.units.sdm.gomoku.mvvm_library.viewmodels;
    exports it.units.sdm.gomoku.ui.gui;
    opens it.units.sdm.gomoku.ui.gui to javafx.fxml;
    exports it.units.sdm.gomoku.ui.gui.viewmodels;
}