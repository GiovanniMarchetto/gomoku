package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.ui.cli.io.InputReader;
import it.units.sdm.gomoku.ui.cli.io.OutputPrinter;
import it.units.sdm.gomoku.ui.support.BooleanAnswers;
import it.units.sdm.gomoku.ui.support.ExposedEnum;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class CLIMain { // TODO : to be tested


    public static void main(String[] args) throws IOException {
        OutputPrinter out = OutputPrinter.getInstance();
        InputReader in = InputReader.getInstance();

        Setup setup = new CLISetup();
        Match matchToDispute = new Match(
                setup.getBoardSizeValue(),
                setup.getNumberOfGames(),
                setup.getPlayers()
        );

        disputeMatch(out, in, setup, matchToDispute);
    }

    private static void disputeMatch(@NotNull final OutputPrinter out,
                                     @NotNull final InputReader in,
                                     @NotNull final Setup setup,
                                     @NotNull final Match matchToDispute) { // TODO : to be uniformed with methods in GUI
        for (int nGame = 1; nGame <= Objects.requireNonNull(matchToDispute).getNumberOfGames(); nGame++) {
            Objects.requireNonNull(out).createNewSection();

            try {

                out.createNewSection();
                out.println("New game!");
                Game currentGame = matchToDispute.startNewGame();

                boolean continuePlaying = true;
                while (continuePlaying) {
                    out.createNewSection();
                    out.println(currentGame.getBoard());
                    Arrays.stream(new Player[]{matchToDispute.getCurrentBlackPlayer(), matchToDispute.getCurrentWhitePlayer()})
                            .forEachOrdered(player -> {
                                try {
                                    Match.executeMoveOfPlayerInGame(
                                            currentGame,
                                            player instanceof CPUPlayer ? ((CPUPlayer) player).chooseRandomEmptyCoordinates(currentGame.getBoard()) : waitForAValidMoveOfAPlayerAndGet(
                                                    currentGame,
                                                    Objects.requireNonNull(setup).getBoardSizeValue()
                                            )
                                    );
                                } catch (Board.PositionAlreadyOccupiedException e) {
                                    // Should never happen (already checked the input)
                                    try {
                                        out.println("Choose an unoccupied position!");
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                } catch (Board.NoMoreEmptyPositionAvailableException e) {
                                    // game ended, will stop at the next iteration
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                    continuePlaying = !currentGame.isThisGameEnded();
                    if (continuePlaying) {
                        out.clearLastSection();
                    }
                }
                out.println("Game ended! ");
                try {
                    out.println(currentGame.getWinner() + " won!");
                } catch (Game.NotEndedGameException e) {
                    out.println("It's a draw!");
                }
                out.println(currentGame.getBoard());

                handleADrawIfIsLastGame(matchToDispute, out, Objects.requireNonNull(in));

                if (nGame < matchToDispute.getNumberOfGames()) {
                    out.print("Press enter to start the new game");
                    in.nextLine();
                    out.clearLastSection();
                } else {
                    out.println("\nMatch ended");
                    out.println("Score: " + matchToDispute.getScore());
                }

            } catch (IOException | Match.MatchEndedException e) {
                Logger.getLogger(CLIMain.class.getCanonicalName())
                        .severe("Exception in class " + CLIMain.class.getCanonicalName() + ": " + e);
            }
        }
    }

    private static void handleADrawIfIsLastGame(@NotNull final Match matchToDispute,
                                                @NotNull final OutputPrinter out,
                                                @NotNull final InputReader in) throws IOException {  // TODO : not tested
        if (Objects.requireNonNull(matchToDispute).isEndedWithADraft()) {
            Objects.requireNonNull(out).print("It's a draw!\n" + "Would you like to play an additional game? " +
                    ExposedEnum.getEnumDescriptionOf(BooleanAnswers.class) + ": ");

            char response = InputReader.checkInputAndGet(
                    () -> Character.toUpperCase(Objects.requireNonNull(in).getACharFromInput()),
                    answer -> ExposedEnum.isValidExposedValueOf(BooleanAnswers.class, String.valueOf(answer)),
                    out,
                    "Insert a valid answer: " + ExposedEnum.getEnumDescriptionOf(BooleanAnswers.class) + ": "
            );
            if (Objects.requireNonNull(ExposedEnum.getEnumValueFromExposedValueOrNull(BooleanAnswers.class, String.valueOf(response)))
                    == BooleanAnswers.YES) {
                matchToDispute.addAnExtraGame();
            }
        }
    }

    @NotNull
    private static Coordinates waitForAValidMoveOfAPlayerAndGet(
            @NotNull final Game game,
            @NotNull final PositiveOddInteger boardSizes) throws IOException {

        OutputPrinter out = OutputPrinter.getInstance();
        InputReader in = InputReader.getInstance();
        out.println("Next move");
        return InputReader.checkInputAndGet(
                () -> {
                    String[] coordNames = {"row position", "column position"};
                    NonNegativeInteger[] xyCoords = new NonNegativeInteger[coordNames.length];
                    for (int i = 0; i < xyCoords.length; i++) {
                        try {
                            out.print("\tInsert " + coordNames[i] + ": ");
                            xyCoords[i] = InputReader.checkInputAndGet(
                                    () -> new NonNegativeInteger(in.getAnIntFromInput()),
                                    coord -> coord.compareTo(boardSizes) < 0,       // TODO : predicate to validate a single coordinate to be added in class Board
                                    out,
                                    "Insert a valid coordinate: "
                            );
                        } catch (IOException e) {
                            Logger.getLogger(CLIMain.class.getCanonicalName())
                                    .severe("Exception: " + e);                             // TODO : refactor: move all duplications of Logger.getLogger() in a single utility function in utils package
                        }
                    }
                    return new Coordinates(xyCoords);
                },
                coordinates -> game.getBoard().getStoneAtCoordinates(coordinates).isNone(),     // TODO : message chain code smell
                out,
                "The position for the specified coordinates is already occupied on the board. Re-insert the coordinates:\n"
        );

    }
}
