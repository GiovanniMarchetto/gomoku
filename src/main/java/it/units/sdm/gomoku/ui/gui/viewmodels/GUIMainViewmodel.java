package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.gui.SceneController;

import static it.units.sdm.gomoku.ui.gui.SceneController.ViewName.*;

public class GUIMainViewmodel extends MainViewmodel {

    @Override
    public void endGame() {
        SceneController.fadeOutSceneIfIsGUIRunningOrDoNothing(SUMMARY_VIEW, 1500);
    }

    @Override
    public void startNewGame() {
        super.startNewGame();
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(MAIN_VIEW);
    }

    @Override
    public void startNewMatch() {
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(START_VIEW);
    }
}