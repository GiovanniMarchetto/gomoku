package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.gui.SceneController;

public class GUIMainViewmodel extends MainViewmodel {

    @Override
    public void endGame() {
        super.endGame();
        SceneController.fadeOutSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.SUMMARY_VIEW, 1500);
    }

    @Override
    public void startNewGame() {
        super.startNewGame();
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.MAIN_VIEW);
    }

    @Override
    public void startNewMatch() {
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.START_VIEW);
    }
}