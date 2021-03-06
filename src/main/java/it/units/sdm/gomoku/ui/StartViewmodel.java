package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Setup;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartViewmodel extends Viewmodel {

    @NotNull
    public static final List<String> boardSizes = Arrays.stream(BoardSizes.values())
            .map(BoardSizes::toString)
            .toList();

    @NotNull
    private final MainViewmodel mainViewmodel;

    @Nullable
    private String player1Name;
    @Nullable
    private String player2Name;
    private boolean player1CPU;
    private double player1CPUSkillFactor;
    private boolean player2CPU;
    private double player2CPUSkillFactor;
    @Nullable
    private String selectedBoardSize;
    @Nullable
    private String numberOfGames;

    public StartViewmodel(@NotNull final MainViewmodel mainViewmodel) {
        this.mainViewmodel = mainViewmodel;
    }

    public void createAndStartMatch() {
        mainViewmodel.createMatchFromSetupAndInitializeNewGame(createSetup());
    }

    @NotNull
    private Setup createSetup() {
        Player player1 = isPlayer1CPU() ? new CPUPlayer(Objects.requireNonNull(getPlayer1Name()), getPlayer1CPUSkillFactor())
                : new HumanPlayer(Objects.requireNonNull(getPlayer1Name()));
        Player player2 = isPlayer2CPU() ? new CPUPlayer(Objects.requireNonNull(getPlayer2Name()), getPlayer2CPUSkillFactor())
                : new HumanPlayer(Objects.requireNonNull(getPlayer2Name()));
        return new Setup(
                player1, player2,
                new PositiveInteger(Integer.parseInt(Objects.requireNonNull(getNumberOfGames()))),
                getSelectedBoardSizeValue());
    }

    //region Getters and Setters
    @Nullable
    public String getPlayer1Name() {
        return player1Name;
    }

    public StartViewmodel setPlayer1Name(@NotNull final String player1Name) {
        this.player1Name = Objects.requireNonNull(player1Name);
        return this;
    }

    @Nullable
    public String getPlayer2Name() {
        return player2Name;
    }

    public StartViewmodel setPlayer2Name(@NotNull final String player2Name) {
        this.player2Name = Objects.requireNonNull(player2Name);
        return this;
    }

    public boolean isPlayer1CPU() {
        return player1CPU;
    }

    public StartViewmodel setPlayer1CPU(boolean player1CPU) {
        this.player1CPU = player1CPU;
        return this;
    }

    public double getPlayer1CPUSkillFactor() {
        return player1CPUSkillFactor;
    }

    public StartViewmodel setPlayer1CPUSkillFactor(@NotNull final String player1CPUSkillFactor) {
        setPlayer1CPUSkillFactor(Double.parseDouble(Objects.requireNonNull(player1CPUSkillFactor)));
        return this;
    }

    public StartViewmodel setPlayer1CPUSkillFactor(double player1CPUSkillFactor) {
        this.player1CPUSkillFactor = player1CPUSkillFactor;
        return this;
    }

    public boolean isPlayer2CPU() {
        return player2CPU;
    }

    public StartViewmodel setPlayer2CPU(boolean player2CPU) {
        this.player2CPU = player2CPU;
        return this;
    }

    public double getPlayer2CPUSkillFactor() {
        return player2CPUSkillFactor;
    }

    public StartViewmodel setPlayer2CPUSkillFactor(@NotNull final String player2CPUSkillFactor) {
        setPlayer2CPUSkillFactor(Double.parseDouble(Objects.requireNonNull(player2CPUSkillFactor)));
        return this;
    }

    public StartViewmodel setPlayer2CPUSkillFactor(double player2CPUSkillFactor) {
        this.player2CPUSkillFactor = player2CPUSkillFactor;
        return this;
    }

    @Nullable
    public String getSelectedBoardSize() {
        return selectedBoardSize;
    }

    public StartViewmodel setSelectedBoardSize(@NotNull final String selectedBoardSize) {
        this.selectedBoardSize = Objects.requireNonNull(selectedBoardSize);
        return this;
    }

    @NotNull
    public PositiveInteger getSelectedBoardSizeValue() {
        return BoardSizes.fromString(Objects.requireNonNull(getSelectedBoardSize())).getBoardSize();
    }

    @Nullable
    public String getNumberOfGames() {
        return numberOfGames;
    }

    public StartViewmodel setNumberOfGames(@NotNull final String numberOfGames) {
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
        return this;
    }
    //endregion
}
