package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.gui.GUISceneController;

import static it.units.sdm.gomoku.ui.gui.GUISceneController.ViewName.*;

public class GUIMainViewmodel extends MainViewmodel {

    @Override
    public void startNewMatch() {
        GUISceneController.passToNewSceneIfIsGUIRunningOrDoNothing(START_VIEW);
    }

    @Override
    public void initializeNewGame() {
        super.initializeNewGame();
        GUISceneController.passToNewSceneIfIsGUIRunningOrDoNothing(MAIN_VIEW);
    }

    @Override
    public void endGame() {
        super.endGame();
        GUISceneController.fadeOutSceneIfIsGUIRunningOrDoNothing(SUMMARY_VIEW, 1500);
    }
}