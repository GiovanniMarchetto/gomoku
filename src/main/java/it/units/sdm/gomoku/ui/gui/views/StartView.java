package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

import java.util.Arrays;

import static it.units.sdm.gomoku.ui.gui.GUIMain.mainViewmodel;

public class StartView extends View {
    @FXML
    private Button startMatchButton;
    @FXML
    private TextField player1TextField;
    @FXML
    private TextField player2TextField;

    @FXML
    private CheckBox cpu1CheckBox;
    @FXML
    private CheckBox cpu2CheckBox;

    @FXML
    private ChoiceBox<String> boardSizeChoiceBox;
    @FXML
    private TextField numberOfGamesTextField;

    public StartView() {
        super(mainViewmodel);
    }

    @FXML
    private void initialize() {
        String[] boardSizeStringList = Arrays.stream(BoardSizes.values()).map(BoardSizes::toString).toArray(String[]::new);
        boardSizeChoiceBox.getItems().addAll(boardSizeStringList);
        boardSizeChoiceBox.setValue(boardSizeStringList[2]);

        checkFieldsAndEnableButton();
        player1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndEnableButton();
        });
        player2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndEnableButton();
        });

        allowOnlyNumberInNumberOfGamesTextField();
    }

    public void startMatchButtonOnMouseClicked(MouseEvent e) {
        var p1 = new Pair<>(player1TextField.getText(), cpu1CheckBox.isSelected());
        var p2 = new Pair<>(player2TextField.getText(), cpu2CheckBox.isSelected());
        var setup = mainViewmodel.createGUISetup(p1, p2, numberOfGamesTextField.getText(), boardSizeChoiceBox.getValue());
        mainViewmodel.createMatchFromSetupAndStartGame(setup);
    }

    private void allowOnlyNumberInNumberOfGamesTextField() {
        numberOfGamesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberOfGamesTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            checkFieldsAndEnableButton();
        });
    }

    private void checkFieldsAndEnableButton() {
        startMatchButton.setDisable(player1TextField.getText().isEmpty() || player2TextField.getText().isEmpty() || numberOfGamesTextField.getText().isEmpty());
    }
}