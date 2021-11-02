package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;
import static it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel.*;


public class StartView extends View {

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

        firePropertyChangeForDefaultValues();

        checkFieldsAndEnableButton();

        addListenerForFirePropertyChange();

        allowOnlyNumberInNumberOfGamesTextField();
    }

    private void addListenerForFirePropertyChange() {
        addTextPropertyListener(player1NameTextField, player1NamePropertyName);
        addTextPropertyListener(player2NameTextField, player2NamePropertyName);
        addCheckBoxListener(player1CPUCheckBox, player1CPUPropertyName);
        addCheckBoxListener(player2CPUCheckBox, player2CPUPropertyName);
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(selectedBoardSizePropertyName, oldValue, newValue));
        addTextPropertyListener(numberOfGamesTextField, numberOfGamesPropertyName);
    }

    private void firePropertyChangeForDefaultValues() {
        firePropertyChange(player1NamePropertyName, null, player1NameTextField.getText());
        firePropertyChange(player2NamePropertyName, null, player2NameTextField.getText());
        firePropertyChange(player1CPUPropertyName, null, player1CPUCheckBox.isSelected());
        firePropertyChange(player2CPUPropertyName, null, player2CPUCheckBox.isSelected());
        firePropertyChange(selectedBoardSizePropertyName, null, boardSizeChoiceBox.getValue());
        firePropertyChange(numberOfGamesPropertyName, null, numberOfGamesTextField.getText());
    }

    private void addTextPropertyListener(TextField textField, String propertyName) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndEnableButton();
            firePropertyChange(propertyName, oldValue, newValue);
        });
    }

    private void addCheckBoxListener(CheckBox checkBox, String propertyName) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(propertyName, oldValue, newValue));
    }

    public void startMatchButtonOnMouseClicked(MouseEvent e) {
        ((StartViewmodel) getViewmodelAssociatedWithView()).startMatch();
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
                        || Objects.equals(player1NameTextField.getText(), player2NameTextField.getText())
                        || numberOfGamesTextField.getText().isEmpty()
                        || Integer.parseInt(numberOfGamesTextField.getText()) == 0);
    }
}