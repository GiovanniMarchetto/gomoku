package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartViewmodel extends Viewmodel {

    // TODO : add nullable/notnull annotations and final to method params

    public static final String numberOfGamesPropertyName = "numberOfGames";
    public static final String selectedBoardSizePropertyName = "selectedBoardSize";

    public static final List<String> boardSizes = Arrays.stream(BoardSizes.values())
            .map(BoardSizes::toString)
            .toList();

    private final AbstractMainViewmodel mainViewmodel;

    private volatile String player1Name;    // TODO : volatile fields?
    private volatile String player2Name;
    private volatile boolean player1CPU;
    private volatile boolean player2CPU;
    private volatile String selectedBoardSize;
    private volatile String numberOfGames;

    public StartViewmodel(AbstractMainViewmodel mainViewmodel) {
        this.mainViewmodel = mainViewmodel;
    }

    public void startMatch() {
        this.mainViewmodel.createMatchFromSetupAndStartGame(createSetup());
    }

    private Setup createSetup() {
        Player player1 = isPlayer1CPU() ? new CPUPlayer(getPlayer1Name()) : new HumanPlayer(getPlayer1Name());
        Player player2 = isPlayer2CPU() ? new CPUPlayer(getPlayer2Name()) : new HumanPlayer(getPlayer2Name());
        return new Setup(player1, player2, new PositiveInteger(Integer.parseInt(getNumberOfGames())), getSelectedBoardSizeValue());
    }

    //region Getters and Setters
    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(@NotNull final String player1Name) {
        this.player1Name = Objects.requireNonNull(player1Name);
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = Objects.requireNonNull(player2Name);
    }

    public boolean isPlayer1CPU() {
        return player1CPU;
    }

    public void setPlayer1CPU(boolean player1CPU) {
        this.player1CPU = player1CPU;
    }

    public boolean isPlayer2CPU() {
        return player2CPU;
    }

    public void setPlayer2CPU(boolean player2CPU) {
        this.player2CPU = player2CPU;
    }

    public String getSelectedBoardSize() {
        return selectedBoardSize;
    }

    public void setSelectedBoardSize(String selectedBoardSize) {
        String oldValue = this.selectedBoardSize;
        if (!Objects.requireNonNull(selectedBoardSize).equals(oldValue)) {
            this.selectedBoardSize = selectedBoardSize;
            firePropertyChange(selectedBoardSizePropertyName, oldValue, selectedBoardSize);
        }
    }

    public PositiveInteger getSelectedBoardSizeValue() {
        return BoardSizes.fromString(getSelectedBoardSize()).getBoardSize();
    }

    public String getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(String numberOfGames) {
        String oldValue = this.numberOfGames;
        if (!Objects.requireNonNull(numberOfGames).equals(oldValue)) {
            this.numberOfGames = numberOfGames;
            firePropertyChange(numberOfGamesPropertyName, oldValue, numberOfGames);
        }
    }
    //endregion

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case selectedBoardSizePropertyName -> setSelectedBoardSize((String) evt.getNewValue());
            case numberOfGamesPropertyName -> setNumberOfGames((String) evt.getNewValue());
        }
    }
}
