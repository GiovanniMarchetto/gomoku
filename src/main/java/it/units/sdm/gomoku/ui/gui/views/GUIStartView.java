package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Objects;
import java.util.function.Consumer;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;
import static it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel.boardSizes;


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
        setDefaultValuesInViewmodel();
        allowOnlyNumberInNumberOfGamesTextField();
        disableStartMatchButtonIfInvalidInputFieldValues();
        addListenerForFirePropertyChange();
    }

    private void addListenerForFirePropertyChange() {
        // TODO : maybe refactor needed
        addTextPropertyListener(player1NameTextField, getViewmodelAssociatedWithView()::setPlayer1Name);
        addTextPropertyListener(player2NameTextField, getViewmodelAssociatedWithView()::setPlayer2Name);
        addSelectedPropertyListener(player1CPUCheckBox, getViewmodelAssociatedWithView()::setPlayer1CPU);
        addSelectedPropertyListener(player2CPUCheckBox, getViewmodelAssociatedWithView()::setPlayer2CPU);
        addSelectedItemPropertyListener(boardSizeChoiceBox, getViewmodelAssociatedWithView()::setSelectedBoardSize);
        addTextPropertyListener(numberOfGamesTextField, getViewmodelAssociatedWithView()::setNumberOfGames);
    }

    private void setDefaultValuesInViewmodel() {
        // TODO : maybe refactor needed
        getViewmodelAssociatedWithView().setPlayer1Name(player1NameTextField.getText());
        getViewmodelAssociatedWithView().setPlayer2Name(player2NameTextField.getText());
        getViewmodelAssociatedWithView().setPlayer1CPU(player1CPUCheckBox.isSelected());
        getViewmodelAssociatedWithView().setPlayer2CPU(player2CPUCheckBox.isSelected());
        getViewmodelAssociatedWithView().setSelectedBoardSize(boardSizeChoiceBox.getValue());
        getViewmodelAssociatedWithView().setNumberOfGames(numberOfGamesTextField.getText());
    }

    private void addTextPropertyListener(TextField textField, Consumer<String> actionOnChange) {
        addPropertyListener(textField.textProperty(), actionOnChange);
    }

    private void addSelectedPropertyListener(CheckBox checkBox, Consumer<Boolean> actionOnChange) {
        addPropertyListener(checkBox.selectedProperty(), actionOnChange);
    }

    private <T> void addSelectedItemPropertyListener(ChoiceBox<T> choiceBox, Consumer<T> actionOnChange) {
        addPropertyListener(choiceBox.getSelectionModel().selectedItemProperty(), actionOnChange);
    }

    private <T> void addPropertyListener(ObservableValue<T> property, Consumer<T> actionOnChange) {
        property.addListener((ignored_observable, ignored_oldValue, newValue) -> {
            disableStartMatchButtonIfInvalidInputFieldValues();
            actionOnChange.accept(newValue);
        });
    }

    public void startMatchButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startMatch();
    }

    private void allowOnlyNumberInNumberOfGamesTextField() {
        try {
            numberOfGamesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    numberOfGamesTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                disableStartMatchButtonIfInvalidInputFieldValues();
            });
        } catch (NumberFormatException ignored) {
            // TODO : handle this exception? / test method opportunely ?
        }
    }

    private void disableStartMatchButtonIfInvalidInputFieldValues() {
        // TODO : test
        boolean invalidNumberOfGamesValue;
        try {
            invalidNumberOfGamesValue = Integer.parseInt(numberOfGamesTextField.getText()) == 0;
        } catch (NumberFormatException e) {
            invalidNumberOfGamesValue = true;
        }
        startMatchButton.setDisable(
                player1NameTextField.getText().isEmpty()
                        || player2NameTextField.getText().isEmpty()
                        || Objects.equals(player1NameTextField.getText(), player2NameTextField.getText())
                        || numberOfGamesTextField.getText().isEmpty()
                        || invalidNumberOfGamesValue);
    }
}