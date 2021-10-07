package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.entities.Board;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TestUtility {

    @NotNull
    public static String createNxNRandomBoardToString(int N) {
        Random random = new Random(0);
        StringBuilder s = new StringBuilder();
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                switch (random.nextInt(3)) {
                    case 0 -> s.append(Board.Stone.BLACK);
                    case 1 -> s.append(Board.Stone.WHITE);
                    default -> s.append(Board.Stone.NONE);
                }
                if(j<N-1) {
                    s.append(",");
                } else {
                    s.append("\n");
                }
            }
        }

        return s.toString();
    }

}
