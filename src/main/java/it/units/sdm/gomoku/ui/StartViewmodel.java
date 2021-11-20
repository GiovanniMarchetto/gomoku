package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Setup;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartViewmodel extends Viewmodel {

    // TODO : add nullable/notnull annotations and final to method params and test

    public static final List<String> boardSizes = Arrays.stream(BoardSizes.values())
            .map(BoardSizes::toString)
            .toList();

    private final MainViewmodel mainViewmodel;

    private volatile String player1Name;    // TODO : volatile fields?
    private volatile String player2Name;
    private volatile boolean player1CPU;
    private volatile double player1CPUNaivety;
    private volatile boolean player2CPU;
    private volatile double player2CPUNaivety;
    private volatile String selectedBoardSize;
    private volatile String numberOfGames;

    public StartViewmodel(MainViewmodel mainViewmodel) {
        this.mainViewmodel = mainViewmodel;
    }

    public void createAndStartMatch() {
        mainViewmodel.createMatchFromSetupAndStartGame(createSetup());
    }

    private Setup createSetup() {
        Player player1 = isPlayer1CPU() ? new CPUPlayer(getPlayer1Name(), player1CPUNaivety) : new HumanPlayer(getPlayer1Name());
        Player player2 = isPlayer2CPU() ? new CPUPlayer(getPlayer2Name(), player2CPUNaivety) : new HumanPlayer(getPlayer2Name());
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

    public void setPlayer2Name(@NotNull final String player2Name) {
        this.player2Name = Objects.requireNonNull(player2Name);
    }

    public boolean isPlayer1CPU() {
        return player1CPU;
    }

    public void setPlayer1CPU(boolean player1CPU) {
        this.player1CPU = player1CPU;
    }

    public double getPlayer1CPUNaivety() {
        return player1CPUNaivety;
    }

    public void setPlayer1CPUNaivety(double player1CPUNaivety) {
        this.player1CPUNaivety = player1CPUNaivety;
    }

    public boolean isPlayer2CPU() {
        return player2CPU;
    }

    public void setPlayer2CPU(boolean player2CPU) {
        this.player2CPU = player2CPU;
    }

    public double getPlayer2CPUNaivety() {
        return player2CPUNaivety;
    }

    public void setPlayer2CPUNaivety(double player2CPUNaivety) {
        this.player2CPUNaivety = player2CPUNaivety;
    }

    public String getSelectedBoardSize() {
        return selectedBoardSize;
    }

    public void setSelectedBoardSize(@NotNull final String selectedBoardSize) {
        this.selectedBoardSize = Objects.requireNonNull(selectedBoardSize);
    }

    public PositiveInteger getSelectedBoardSizeValue() {
        return BoardSizes.fromString(getSelectedBoardSize()).getBoardSize();
    }

    public String getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(@NotNull final String numberOfGames) {
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
    }

    private void setFieldValuesFromSetupWithoutSetters(@NotNull final Setup setup) {    // TODO : test
        // TODO : needed?
        player1Name = setup.player1().getName();
        player2Name = setup.player2().getName();
        player1CPU = setup.player1() instanceof CPUPlayer;
        player2CPU = setup.player2() instanceof CPUPlayer;
        selectedBoardSize = BoardSizes.fromBoardSizeValue(setup.boardSize())
                .orElseThrow(IllegalArgumentException::new)
                .toString();
        numberOfGames = setup.numberOfGames().toString();
    }
    //endregion

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
