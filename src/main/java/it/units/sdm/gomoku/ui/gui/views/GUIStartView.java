package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Objects;
import java.util.function.Consumer;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;
import static it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel.*;


public class GUIStartView extends View<StartViewmodel> {

    // TODO : add notnull / nullable annotations and final in method params

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

    public GUIStartView() {
        super(new StartViewmodel(guiMainViewmodel));
    }

    @FXML
    private void initialize() {// TODO : refactor this method
        boardSizeChoiceBox.getItems().addAll(boardSizes);
        boardSizeChoiceBox.setValue(boardSizes.get(boardSizes.size() / 2));

        firePropertyChangeForDefaultValues();

        disableStartMatchButtonIfInvalidInputFieldValues();

        addListenerForFirePropertyChange();

        allowOnlyNumberInNumberOfGamesTextField();
    }

    private void addListenerForFirePropertyChange() {
        addTextPropertyListener(player1NameTextField, player1NewName -> getViewmodelAssociatedWithView().setPlayer1Name(player1NewName));
        addTextPropertyListener(player2NameTextField, player2NamePropertyName);
        addSelectedPropertyListener(player1CPUCheckBox, player1CPUPropertyName);
        addSelectedPropertyListener(player2CPUCheckBox, player2CPUPropertyName);
        boardSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(selectedBoardSizePropertyName, oldValue, newValue));
        addTextPropertyListener(numberOfGamesTextField, numberOfGamesPropertyName);
    }

    private void firePropertyChangeForDefaultValues() {
        getViewmodelAssociatedWithView().setPlayer1Name(player1NameTextField.getText());
        firePropertyChange(player2NamePropertyName, player2NameTextField.getText());
        firePropertyChange(player1CPUPropertyName, player1CPUCheckBox.isSelected());
        firePropertyChange(player2CPUPropertyName, player2CPUCheckBox.isSelected());
        firePropertyChange(selectedBoardSizePropertyName, boardSizeChoiceBox.getValue());
        firePropertyChange(numberOfGamesPropertyName, numberOfGamesTextField.getText());
    }

    private void addTextPropertyListener(TextField textField, String propertyName) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            disableStartMatchButtonIfInvalidInputFieldValues();
            firePropertyChange(propertyName, oldValue, newValue);
        });
    }

    private void addTextPropertyListener(TextField textField, Consumer<String> actionOnChange) {
        addPropertyListener(textField.textProperty(), actionOnChange);
    }

    private <T> void addPropertyListener(Property<T> property, Consumer<T> actionOnChange) {
        property.addListener((ignored_observable, ignored_oldValue, newValue) -> {
            disableStartMatchButtonIfInvalidInputFieldValues();
            actionOnChange.accept(newValue);
        });
    }

    private void addSelectedPropertyListener(CheckBox checkBox, String propertyName) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                firePropertyChange(propertyName, oldValue, newValue));
    }

    public void startMatchButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startMatch();
    }

    private void allowOnlyNumberInNumberOfGamesTextField() {
        numberOfGamesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberOfGamesTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            disableStartMatchButtonIfInvalidInputFieldValues();
        });
    }

    private void disableStartMatchButtonIfInvalidInputFieldValues() {
        startMatchButton.setDisable(
                player1NameTextField.getText().isEmpty()
                        || player2NameTextField.getText().isEmpty()
                        || Objects.equals(player1NameTextField.getText(), player2NameTextField.getText())
                        || numberOfGamesTextField.getText().isEmpty()
                        || Integer.parseInt(numberOfGamesTextField.getText()) == 0);
    }
}