module it.units.sdm.gomoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.logging;
    //noinspection Java9RedundantRequiresStatement //needed
    requires org.json;
    requires java.desktop;

    opens it.units.sdm.gomoku to javafx.fxml;
    opens it.units.sdm.gomoku.model.entities to javafx.fxml;
    opens it.units.sdm.gomoku.mvvm_library to javafx.fxml;
    opens it.units.sdm.gomoku.ui.gui.views to javafx.fxml;
    opens it.units.sdm.gomoku.ui.support to javafx.fxml;
    opens it.units.sdm.gomoku.ui.gui to javafx.fxml;
    opens it.units.sdm.gomoku.ui to javafx.fxml;

    exports it.units.sdm.gomoku;
    exports it.units.sdm.gomoku.model.entities;
    exports it.units.sdm.gomoku.model.exceptions;
    exports it.units.sdm.gomoku.model.custom_types;
    exports it.units.sdm.gomoku.mvvm_library;
    exports it.units.sdm.gomoku.ui.cli;
    exports it.units.sdm.gomoku.ui.gui;
    exports it.units.sdm.gomoku.ui.gui.views;
    exports it.units.sdm.gomoku.ui.gui.viewmodels;
    exports it.units.sdm.gomoku.ui.support;
    exports it.units.sdm.gomoku.ui;
    exports it.units.sdm.gomoku.ui.exceptions;
    exports it.units.sdm.gomoku.ui.cli.viewmodels;
    exports it.units.sdm.gomoku.ui.cli.views;
    exports it.units.sdm.gomoku.property_change_handlers;
    exports it.units.sdm.gomoku.property_change_handlers.observable_properties;
    exports it.units.sdm.gomoku.utils;
    opens it.units.sdm.gomoku.utils to javafx.fxml;
}