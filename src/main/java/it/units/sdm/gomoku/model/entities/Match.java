package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Main;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.utils.BufferCoordinates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Match {

    @NotNull
    private final static PositiveInteger DEFAULT_MAXIMUM_GAMES = new PositiveInteger(1);

    @NotNull
    private final List<Game> gameList;

    @NotNull
    private final PositiveInteger boardSize;

    @NotNull
    private final PositiveInteger howManyGames;

    @NotNull
    private Player currentBlackPlayer,
            currentWhitePlayer;

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger howManyGames) {
        this.currentBlackPlayer = Objects.requireNonNull(player2);
        this.currentWhitePlayer = Objects.requireNonNull(player1);
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.howManyGames = Objects.requireNonNull(howManyGames);
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize, @PositiveIntegerType int howManyGames) {
        this(player1, player2, new PositiveInteger(boardSize), new PositiveInteger(howManyGames));
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize) {
        this(player1, player2, boardSize, DEFAULT_MAXIMUM_GAMES.intValue());
    }

    public static void executeMoveOfPlayerInGame(@NotNull Player player, @NotNull Game game, @Nullable Coordinates coordinatesOfTheMove)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {

        if (player instanceof CPUPlayer) {  // TODO : refactor to avoid coupling?
            coordinatesOfTheMove = ((CPUPlayer) player).chooseRandomEmptyCoordinates(game.getBoard());
        }

        assert coordinatesOfTheMove != null;
        game.placeStone(player, coordinatesOfTheMove);
    }

    @NotNull
    public Game startNewGame() {
        invertCurrentPlayersColors();
        Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
        gameList.add(newGame);
        return newGame;
    }

    private void invertCurrentPlayersColors() {
        Player oldWhitePlayer = currentWhitePlayer;
        currentWhitePlayer = currentBlackPlayer;
        currentBlackPlayer = oldWhitePlayer;
    }

    @NotNull
    public Map<Player, NonNegativeInteger> getScore() {
        Map<Player, NonNegativeInteger> score = new HashMap<>(2);
        score.put(currentBlackPlayer, getScoreOfPlayer(currentBlackPlayer));
        score.put(currentWhitePlayer, getScoreOfPlayer(currentWhitePlayer));
        return score;
    }

    @NotNull
    private NonNegativeInteger getScoreOfPlayer(@NotNull Player player) {
        return new NonNegativeInteger(
                (int) gameList.stream()
                        .filter(aGame -> {
                            try {
                                return aGame.getWinner() == Objects.requireNonNull(player);
                            } catch (Game.NotEndedGameException e) {
                                return false;
                            }
                        })
                        .count()
        );
    }

    public void disputeMatch(@NotNull final BufferCoordinates bufferCoordinates) {
        Main.MatchDisputer matchDisputer = new Main.MatchDisputer(bufferCoordinates, this);
        matchDisputer.start();
        try {
            matchDisputer.join();                                                                                       // TODO : try-catch or throws in method signature?
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bufferCoordinates.clear();
        }
    }

    @NotNull
    public Board getBoardOfNthGame(@NotNull NonNegativeInteger nOfGame_from0ToNOfDisputedGamesTillNow) {
        int indexOfGame = Objects.requireNonNull(nOfGame_from0ToNOfDisputedGamesTillNow).intValue();
        int numberOfCurrentlyDisputedMatch = gameList.size();   // TODO : concurrency problems?
        if (indexOfGame < numberOfCurrentlyDisputedMatch) {
            return gameList.get(indexOfGame).getBoard();
        } else {
            throw new IllegalArgumentException(
                    "Till now, " + numberOfCurrentlyDisputedMatch + " games were disputed" +
                            (numberOfCurrentlyDisputedMatch > 0 ?
                                    (", hence the parameter for this method must be between " + 0 + " (inclusive) and " +
                                            (numberOfCurrentlyDisputedMatch - 1) + " (inclusive), but ") :
                                    ". ")
                            + indexOfGame + " was provided.");
        }
    }

    public int getHowManyGames() {
        return howManyGames.intValue();
    }

    @NotNull
    public Player getCurrentBlackPlayer() {
        return currentBlackPlayer;
    }

    @NotNull
    public Player getCurrentWhitePlayer() {
        return currentWhitePlayer;
    }

}
