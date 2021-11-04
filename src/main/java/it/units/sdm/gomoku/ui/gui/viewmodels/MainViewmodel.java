package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.gui.SceneController;

public class MainViewmodel extends AbstractMainViewmodel {

    @Override
    public void endGame() {
        super.endGame();
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.SUMMARY_VIEW);
    }

    @Override
    public void startNewGame() {
        super.startNewGame();
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.MAIN_VIEW);
        forceReFireAllCells();
    }

    @Override
    public void startNewMatch() {
        SceneController.passToNewSceneIfIsGUIRunningOrDoNothing(SceneController.ViewName.START_VIEW);
    }
}