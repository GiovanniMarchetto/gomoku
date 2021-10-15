package it.units.sdm.gomoku;

import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.*;

import java.lang.reflect.Field;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Player man = new Player("Fabio");
        CPUPlayer cpu = new CPUPlayer("CPU");
        final int NUMBER_OF_GAMES = 11;
        final int BOARD_SIZE = 19;

        Match match = new Match(man, cpu, BOARD_SIZE, NUMBER_OF_GAMES);

        Match.BufferCoordinates bufferCoordinates = new Match.BufferCoordinates();
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

        System.out.println("SCORE: \t" + match.getScore());

    }
}

