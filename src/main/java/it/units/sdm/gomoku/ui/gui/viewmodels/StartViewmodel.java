package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.Setup;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartViewmodel extends Viewmodel {

    public static final String player1NamePropertyName = "player1Name";
    public static final String player2NamePropertyName = "player2Name";
    public static final String player1CPUPropertyName = "player1CPU";
    public static final String player2CPUPropertyName = "player2CPU";
    public static final String numberOfGamesPropertyName = "numberOfGames";
    public static final String selectedBoardSizePropertyName = "selectedBoardSize";

    public static final List<String> boardSizes = Arrays.stream(BoardSizes.values())
            .map(BoardSizes::toString)
            .toList();

    private final AbstractMainViewmodel mainViewmodel;

    private String player1Name;
    private String player2Name;
    private boolean player1CPU;
    private boolean player2CPU;
    private String selectedBoardSize;
    private String numberOfGames;

    public StartViewmodel(AbstractMainViewmodel mainViewmodel) {
        this.mainViewmodel = mainViewmodel;
    }

    public void startMatch() {
        this.mainViewmodel.createMatchFromSetupAndStartGame(createSetup());
    }

    private Setup createSetup() {
        Player player1 = isPlayer1CPU() ? new CPUPlayer(getPlayer1Name()) : new Player(getPlayer1Name());
        Player player2 = isPlayer2CPU() ? new CPUPlayer(getPlayer2Name()) : new Player(getPlayer2Name());
        return new Setup(player1, player2, new PositiveInteger(Integer.parseInt(getNumberOfGames())), getSelectedBoardSizeValue());
    }

    //region Getters and Setters
    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        String oldValue = this.player1Name;
        if (!Objects.requireNonNull(player1Name).equals(oldValue)) {
            this.player1Name = player1Name;
            firePropertyChange(player1NamePropertyName, oldValue, player1Name);
        }
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        String oldValue = this.player2Name;
        if (!Objects.requireNonNull(player2Name).equals(oldValue)) {
            this.player2Name = player2Name;
            firePropertyChange(player2NamePropertyName, oldValue, player2Name);
        }
    }

    public boolean isPlayer1CPU() {
        return player1CPU;
    }

    public void setPlayer1CPU(boolean player1CPU) {
        boolean oldValue = this.player1CPU;
        if (player1CPU != oldValue) {
            this.player1CPU = player1CPU;
            firePropertyChange(player1CPUPropertyName, oldValue, player1CPU);
        }
    }

    public boolean isPlayer2CPU() {
        return player2CPU;
    }

    public void setPlayer2CPU(boolean player2CPU) {
        boolean oldValue = this.player2CPU;
        if (player2CPU != oldValue) {
            this.player2CPU = player2CPU;
            firePropertyChange(player2CPUPropertyName, oldValue, player2CPU);
        }
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
            case player1NamePropertyName -> setPlayer1Name((String) evt.getNewValue());
            case player2NamePropertyName -> setPlayer2Name((String) evt.getNewValue());
            case player1CPUPropertyName -> setPlayer1CPU((Boolean) evt.getNewValue());
            case player2CPUPropertyName -> setPlayer2CPU((Boolean) evt.getNewValue());
            case selectedBoardSizePropertyName -> setSelectedBoardSize((String) evt.getNewValue());
            case numberOfGamesPropertyName -> setNumberOfGames((String) evt.getNewValue());
        }
    }
}
