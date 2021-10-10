package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.custom_types.PositiveInteger;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

import static it.units.sdm.gomoku.custom_types.PositiveInteger.PositiveIntegerType;

public class Match {

    @NotNull
    private final static PositiveInteger DEFAULT_MAXIMUM_GAMES = new PositiveInteger(1);

    @NotNull
    private final List<Game> gameList;

    @NotNull
    private final Pair<Player, Player> players;

    @NotNull
    private final PositiveInteger boardSize;

    @NotNull
    private PositiveInteger howManyGames;

    @NotNull
    private Player currentBlackPlayer,
            currentWhitePlayer;

    public Match(@NotNull final Pair<@NotNull Player, @NotNull Player> players, @NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger howManyGames) {
        this.players = Objects.requireNonNull(players);
        Objects.requireNonNull(players.getKey());
        Objects.requireNonNull(players.getValue());
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.howManyGames = Objects.requireNonNull(howManyGames);
        this.currentBlackPlayer = players.getKey();
        this.currentWhitePlayer = players.getValue();
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2, @PositiveIntegerType int boardSize, @PositiveIntegerType int howManyGames) {
        this(new Pair<>(player1, player2), new PositiveInteger(boardSize), new PositiveInteger(howManyGames));
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2, @PositiveIntegerType int boardSize) {
        this(player1, player2, boardSize, DEFAULT_MAXIMUM_GAMES.intValue());
    }

    @NotNull
    private Game startNewGame() {
        invertCurrentBlackWhitePlayersOrInitialize();
        Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
        gameList.add(newGame);
        return newGame;
    }

    private void invertCurrentBlackWhitePlayersOrInitialize() {
        Player oldWhitePlayer = currentWhitePlayer;
        currentWhitePlayer = currentBlackPlayer;
        currentBlackPlayer = oldWhitePlayer;
    }

    @NotNull
    public Map<Player, NonNegativeInteger> getScore() {
        Map<Player, NonNegativeInteger> score = new HashMap<>(2);
        Player player1 = players.getKey(),
                player2 = players.getValue();
        score.put(player1, getScoreOfPlayer(player1));
        score.put(player2, getScoreOfPlayer(player2));
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

    public static class BufferCoordinates {
        private Coordinates bufferCoordinate;

        private boolean isPresent() {
            return bufferCoordinate != null;
        }

        private void clear() {
            bufferCoordinate = null;
        }

        private void wait_() {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Interrupted");  // TODO : better to use try-catch or throws in method signature?
            }
        }

        public synchronized void insert(@NotNull Coordinates coordinates) {
            while (isPresent()) {                          // TODO : waste time?
                wait_();
            }
            this.bufferCoordinate = coordinates;
            notify();
        }

        public synchronized Coordinates getAndRemove() {
            while (!isPresent()) {                          // TODO : waste time?
                wait_();
            }
            Coordinates coord = bufferCoordinate;
            clear();
            notify();
            return coord;
        }
    }

    public void disputeMatch(@NotNull final BufferCoordinates bufferCoordinates) {
        MatchDisputer matchDisputer = new MatchDisputer(bufferCoordinates, this);
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

    private static void executeMoveOfPlayerInGame(@NotNull Player player, @NotNull Game game, @Nullable Coordinates coordinatesOfTheMove)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {

        if (player instanceof CPUPlayer) {  // TODO : refactor to avoid coupling?
            Board.Stone cpuStone = game.getBlackPlayer().equals(player) ? Board.Stone.BLACK : Board.Stone.WHITE;
            coordinatesOfTheMove = ((CPUPlayer) player).chooseRandomCoordinates(game.getBoard());
        }

        assert coordinatesOfTheMove != null;
        game.placeStone(player, coordinatesOfTheMove);
    }


    private static class MatchDisputer extends Thread {
        private final BufferCoordinates buffer;
        private final Match matchToDispute;

        public MatchDisputer(@NotNull BufferCoordinates buffer, @NotNull Match matchToDispute) {
            this.buffer = Objects.requireNonNull(buffer);
            this.matchToDispute = Objects.requireNonNull(matchToDispute);
        }

        @Override
        public void run() {
            BiFunction<Game, BufferCoordinates, Coordinates> waitForAValidMoveOfAPlayerAndGet = (game, buffer) -> {     // TODO : better to use a static method? Refactor?
                Coordinates coordOfMoveOfPlayer;
                do {
                    coordOfMoveOfPlayer = buffer.getAndRemove();
                } while (!game.getBoard().getStoneAtCoordinates(coordOfMoveOfPlayer).isNone());  // TODO : message chain code smell
                return coordOfMoveOfPlayer;
            };

            for (int nGame = 1; nGame <= matchToDispute.howManyGames.intValue(); nGame++) {

                Game currentGame = matchToDispute.startNewGame();
                System.out.println("New game!");                                                                        // TODO : delete this
                while (!currentGame.isThisGameEnded()) {
                    try {
                        executeMoveOfPlayerInGame(matchToDispute.currentBlackPlayer, currentGame, waitForAValidMoveOfAPlayerAndGet.apply(currentGame, buffer));
                        executeMoveOfPlayerInGame(matchToDispute.currentWhitePlayer, currentGame, waitForAValidMoveOfAPlayerAndGet.apply(currentGame, buffer));
                    } catch (Board.PositionAlreadyOccupiedException e) {
                        System.out.println("Choose an unoccupied position!");                                           // TODO : delete this
                    } catch (Board.NoMoreEmptyPositionAvailableException ignored) {
                    }
                }
                System.out.println(currentGame.getBoard());                                                             // TODO : delete this
                System.out.print("Game ended! ");                                                                       // TODO : delete this
                try {
                    System.out.println(currentGame.getWinner() + " won!");                                              // TODO : delete this
                } catch (Game.NotEndedGameException e) {
                    System.out.println("It's a draw!");                                                                 // TODO : delete this
                }

                // TODO : draw not handled yet (players may decide to dispute a spare game)
//                if (nGame == matchToDispute.howManyGames.intValue()) {
//                    if (matchToDispute.getScoreOfPlayer(matchToDispute.currentBlackPlayer)
//                            .equals(matchToDispute.getScoreOfPlayer(matchToDispute.currentWhitePlayer))) {
//                        System.out.println("It's a draw!\n" +
//                                "Would you like to play an additional game? (Y/N)");
//                        char response = scanner.nextLine().charAt(0);
//                        if (response == 'Y' || response == 'y') {
//                            howManyGames = new PositiveInteger(howManyGames.intValue() + 1);
//                        }
//                    }
//                }
            }
        }
    }

}
