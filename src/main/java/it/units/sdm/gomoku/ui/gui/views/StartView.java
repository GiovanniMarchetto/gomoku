package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;
import static it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel.*;


public class StartView extends View<StartViewmodel> {

    @FXML
    private Button startMatchButton;

    @FXML
    private TextField player1NameTextField;
    @FXML
    private TextField player2NameTextField;
    @FXML
    private CheckBox player1CPUCheckBox;
    @FXML
    private CheckBox player2CPUCheckBox;
    @FXML
    private ChoiceBox<String> boardSizeChoiceBox;
    @FXML
    private TextField numberOfGamesTextField;

    public StartView() {
        super(new StartViewmodel(guiMainViewmodel));
    }

    @FXML
    private void initialize() {// TODO : refactor this method
        boardSizeChoiceBox.getItems().addAll(boardSizes);
        boardSizeChoiceBox.setValue(boardSizes.get(boardSizes.size() / 2));

        firePropertyChange(player1NamePropertyName, null, player1NameTextField.getText());
        firePropertyChange(player2NamePropertyName, null, player2NameTextField.getText());
        firePropertyChange(player1CPUPropertyName, null, player1CPUCheckBox.isSelected());
        firePropertyChange(player2CPUPropertyName, null, player2CPUCheckBox.isSelected());
        firePropertyChange(selectedBoardSizePropertyName, null, boardSizeChoiceBox.getValue());
        firePropertyChange(numberOfGamesPropertyName, null, numberOfGamesTextField.getText());


        checkFieldsAndEnableButton();
        player1NameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndEnableButton();
            firePropertyChange(player1NamePropertyName, oldValue, newValue);
        });
        player2NameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndEnableButton();
            firePropertyChange(player2NamePropertyName, oldValue, newValue);
        });
        player1CPUCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(player1CPUPropertyName, oldValue, newValue));
        player2CPUCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(player2CPUPropertyName, oldValue, newValue));
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(selectedBoardSizePropertyName, oldValue, newValue));
        numberOfGamesTextField.textProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(numberOfGamesPropertyName, oldValue, newValue));

        allowOnlyNumberInNumberOfGamesTextField();
    }

    public void startMatchButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startMatch();
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
        startMatchButton.setDisable(
                player1NameTextField.getText().isEmpty()
                        || player2NameTextField.getText().isEmpty()
                        || numberOfGamesTextField.getText().isEmpty());
    }
}