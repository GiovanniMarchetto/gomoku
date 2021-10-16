package it.units.sdm.gomoku;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.model.utils.BufferCoordinates;
import it.units.sdm.gomoku.ui.cli.OutputPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class Main {

    public static OutputPrinter out = OutputPrinter.getPrinter(Charset.defaultCharset());

    public static void main(String[] args) throws IOException {

        try {
            out.println("\nHellooo");
            out.clearAll();
            out.println("World");
            out.createNewSection();
            out.println("Hello 2");
            out.print("Pippo");
            out.clearLastSection();
            out.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }


        Player man = new Player("Fabio");
        CPUPlayer cpu = new CPUPlayer("CPU");
        final int NUMBER_OF_GAMES = 11;
        final int BOARD_SIZE = 19;

        Match match = new Match(man, cpu, BOARD_SIZE, NUMBER_OF_GAMES);

        BufferCoordinates bufferCoordinates = new BufferCoordinates();
        Thread boardOccupier = new Thread(() -> {
            while (true) {  // infinite loop
                try {
                    int gameIndex = 0;
                    try {
                        Field field = match.getClass().getDeclaredField("gameList");
                        field.setAccessible(true);
                        @SuppressWarnings("unchecked") List<Game> game = (List<Game>) field.get(match);
                        gameIndex = game.size() - 1;
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    bufferCoordinates.insert(cpu.chooseRandomEmptyCoordinates(match.getBoardOfNthGame(new NonNegativeInteger(gameIndex))));
                } catch (IllegalArgumentException | Board.NoMoreEmptyPositionAvailableException ignored) {
                }
            }
        });                                                                                                             // TODO : this will be done by the real human which has to choose the position and the entire class BoardOccupier should go away

        boardOccupier.start();
        match.disputeMatch(bufferCoordinates);
        boardOccupier.stop();                                                                                           // TODO: deprecated, but this thread is here only for occupy random positions on the board (what the human player would do).

        out.print(("SCORE: \t" + match.getScore() + "\n").getBytes());

    }

    public static class MatchDisputer extends Thread {
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

            for (int nGame = 1; nGame <= matchToDispute.getHowManyGames(); nGame++) {
                out.createNewSection();

                try {

                    Game currentGame = matchToDispute.startNewGame();
                    out.println("New game!");                                                                        // TODO : delete this
                    while (!currentGame.isThisGameEnded()) {
                        try {
                            Match.executeMoveOfPlayerInGame(matchToDispute.getCurrentBlackPlayer(), currentGame, waitForAValidMoveOfAPlayerAndGet.apply(currentGame, buffer));
                            Match.executeMoveOfPlayerInGame(matchToDispute.getCurrentWhitePlayer(), currentGame, waitForAValidMoveOfAPlayerAndGet.apply(currentGame, buffer));
                        } catch (Board.PositionAlreadyOccupiedException e) {
                            out.println("Choose an unoccupied position!");                                           // TODO : delete this
                        } catch (Board.NoMoreEmptyPositionAvailableException ignored) {
                        }
                    }
                    out.println(currentGame.getBoard());                                                             // TODO : delete this
                    out.print("Game ended! ");                                                                       // TODO : delete this
                    try {
                        out.println(currentGame.getWinner() + " won!");                                              // TODO : delete this
                    } catch (Game.NotEndedGameException e) {
                        out.println("It's a draw!");                                                                 // TODO : delete this
                    }

                    // TODO : draw not handled yet (players may decide to dispute a spare game)
//                if (nGame == matchToDispute.howManyGames.intValue()) {
//                    if (matchToDispute.getScoreOfPlayer(matchToDispute.currentBlackPlayer)
//                            .equals(matchToDispute.getScoreOfPlayer(matchToDispute.currentWhitePlayer))) {
//                        out.println("It's a draw!\n" +
//                                "Would you like to play an additional game? (Y/N)");
//                        char response = scanner.nextLine().charAt(0);
//                        if (response == 'Y' || response == 'y') {
//                            howManyGames = new PositiveInteger(howManyGames.intValue() + 1);
//                        }
//                    }
//                }
                } catch (IOException e) {
                    Logger.getLogger(getClass().getCanonicalName())
                            .severe("Exception in class " + getClass().getCanonicalName() +
                                    " in thread " + Thread.currentThread().getName() + ": " + e);
                }
                try {
                    Thread.sleep(2000);                                                                         // TODO : delete this (only to see how the output changes)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                out.clearLastSection();
            }
        }
    }
}