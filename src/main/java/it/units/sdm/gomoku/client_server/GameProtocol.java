package it.units.sdm.gomoku.client_server;

public class GameProtocol {

    public static final int SERVER_PORT_NUMBER = 9999;
    public static final int NUMBER_OF_PLAYERS = 2;

    private enum Status {
        WAITING_FOR_FIRST_CLIENT_TO_CONNECT_AND_SETUP,
        WAITING_FOR_SECOND_CLIENT_TO_CONNECT_AND_UPDATED_SETUP,
        MATCH_STARTED,
        FIRST_PLAYER_PLACED,
        SECOND_PLAYER_PLACED,
        GAME_ENDED,
        MATCH_ENDED

    }

    private Status currentStatus = Status.WAITING_FOR_FIRST_CLIENT_TO_CONNECT_AND_SETUP;


    public void processInput(Object input) {
        throw new UnsupportedOperationException("Not implemented yet");
//        switch(currentStatus) {
//            case WAITING_FOR_FIRST_CLIENT_TO_CONNECT_AND_SETUP -> {
//                if(input instanceof Setup) {
//                } else {
//                    throw new IllegalArgumentException("");
//                }
//            }
//        }
    }

}