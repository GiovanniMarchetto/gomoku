package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.mvvm_library.views.gui_items.CommanderButton;
import it.units.sdm.gomoku.ui.gui.GUISetup;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.PlayerTypes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static it.units.sdm.gomoku.MainApplication.mainViewmodel;
import static it.units.sdm.gomoku.ui.support.Setup.setupCompletedPropertyName;

public class StartView extends View {

    @FXML
    private TextField player1;
    @FXML
    private TextField player2;

    @FXML
    private CheckBox cpu1CheckBox;
    @FXML
    private CheckBox cpu2CheckBox;

    @FXML
    private ChoiceBox<String> boardSizeList;

    @FXML
    private TextField howManyGames;

    @FXML
    private HBox boxForButton;

    private Button confirmButton;


    public StartView() {
        super(mainViewmodel);
    }

    private CommanderButton getButtonFirePropertyChange() {
        return new CommanderButton(
                "Start Match!",
                this,
                getViewmodelAssociatedWithView(),
                setupCompletedPropertyName,
                () -> {
                    Map<Player, PlayerTypes> players = Arrays.stream(
                                    new Pair[]{
                                            new Pair<>(cpu1CheckBox, player1),
                                            new Pair<>(cpu2CheckBox, player2)
                                    }
                            )
                            .map(pair -> {
                                CheckBox cpuCheckBox = ((CheckBox) pair.getKey());
                                String playerName = ((TextField) pair.getValue()).getText();

                                if (cpuCheckBox.isSelected()) {
                                    return new AbstractMap.SimpleEntry<>(new CPUPlayer(playerName), PlayerTypes.CPU);
                                } else {
                                    return new AbstractMap.SimpleEntry<>(new Player(playerName), PlayerTypes.PERSON);
                                }
                            })
                            .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

                    PositiveInteger numberOfGames = new PositiveInteger(Integer.parseInt(howManyGames.getText()));
                    PositiveOddInteger boardSize = BoardSizes.fromString(boardSizeList.getValue()).getBoardSize();

                    return new GUISetup(players, numberOfGames, boardSize);
                });
    }

    @FXML
    private void initialize() {

        CommanderButton commanderButton = getButtonFirePropertyChange();
        confirmButton = commanderButton.getGUIItem();
        boxForButton.getChildren().add(confirmButton);

        String[] boardSizeStringList = Arrays.stream(BoardSizes.values()).map(BoardSizes::toString).toArray(String[]::new);
        boardSizeList.getItems().addAll(boardSizeStringList);
        boardSizeList.setValue(boardSizeStringList[2]);

        CheckFieldsAndEnableButton();
        player1.textProperty().addListener((observable, oldValue, newValue) ->{
            CheckFieldsAndEnableButton();
        });
        player2.textProperty().addListener((observable, oldValue, newValue) ->{
            CheckFieldsAndEnableButton();
        });
        howManyGames.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                howManyGames.setText(newValue.replaceAll("[^\\d]", ""));
            }
            CheckFieldsAndEnableButton();
        });
    }

    private void CheckFieldsAndEnableButton() {
        confirmButton.setDisable(player1.getText().isEmpty() || player2.getText().isEmpty() || howManyGames.getText().isEmpty());
    }
}