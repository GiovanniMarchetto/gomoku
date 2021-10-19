package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.mvvm_library.views.gui_items.CommanderButton;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
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
        var vm = (MainViewmodel) getViewmodelAssociatedWithView();
        if (!vm.isMatchEnded()) {
            CommanderButton continueButton = getContinueButtonFirePropertyChange();
            mainVBox.getChildren().add(continueButton.getGUIItem());
        }
    }

    private CommanderButton getContinueButtonFirePropertyChange() {
        return new CommanderButton(
                "Continue",
                this,
                getViewmodelAssociatedWithView(),
                continueAfterSummaryPropertyName,
                () -> null);
    }

}
