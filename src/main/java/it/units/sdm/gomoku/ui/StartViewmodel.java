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

    private String player1Name;
    private String player2Name;
    private boolean player1CPU;
    private double player1CPUSkillFactor;
    private boolean player2CPU;
    private double player2CPUSkillFactor;
    private String selectedBoardSize;
    private String numberOfGames;

    public StartViewmodel(MainViewmodel mainViewmodel) {
        this.mainViewmodel = mainViewmodel;
    }

    public void createAndStartMatch() {
        mainViewmodel.createMatchFromSetupAndStartGame(createSetup());
    }

    private Setup createSetup() {
        Player player1 = isPlayer1CPU() ? new CPUPlayer(getPlayer1Name(), player1CPUSkillFactor) : new HumanPlayer(getPlayer1Name());
        Player player2 = isPlayer2CPU() ? new CPUPlayer(getPlayer2Name(), player2CPUSkillFactor) : new HumanPlayer(getPlayer2Name());
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

    public double getPlayer1CPUSkillFactor() {
        return player1CPUSkillFactor;
    }

    public void setPlayer1CPUSkillFactor(@NotNull final String player1CPUSkillFactor) {
        setPlayer1CPUSkillFactor(Double.parseDouble(Objects.requireNonNull(player1CPUSkillFactor)));
    }

    public void setPlayer1CPUSkillFactor(double player1CPUSkillFactor) {
        this.player1CPUSkillFactor = player1CPUSkillFactor;
    }

    public boolean isPlayer2CPU() {
        return player2CPU;
    }

    public void setPlayer2CPU(boolean player2CPU) {
        this.player2CPU = player2CPU;
    }

    public double getPlayer2CPUSkillFactor() {
        return player2CPUSkillFactor;
    }

    public void setPlayer2CPUSkillFactor(@NotNull final String player2CPUSkillFactor) {
        setPlayer2CPUSkillFactor(Double.parseDouble(Objects.requireNonNull(player2CPUSkillFactor)));
    }

    public void setPlayer2CPUSkillFactor(double player2CPUSkillFactor) {
        this.player2CPUSkillFactor = player2CPUSkillFactor;
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
