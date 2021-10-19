package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.mvvm_library.views.gui_items.CommanderButton;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import static it.units.sdm.gomoku.ui.gui.Main.mainViewmodel;

public class SummaryView extends View {

    public static final String continueAfterSummaryPropertyName = "continue";

    @FXML
    private VBox mainVBox;

    public SummaryView() {
        super(mainViewmodel);
    }

    @FXML
    private void initialize() {
        CommanderButton continueButton = getButtonFirePropertyChange();
        mainVBox.getChildren().add(continueButton.getGUIItem());
    }

    private CommanderButton getButtonFirePropertyChange() {
        return new CommanderButton(
                "Continue",
                this,
                getViewmodelAssociatedWithView(),
                continueAfterSummaryPropertyName,
                ()->null);
    }

}
