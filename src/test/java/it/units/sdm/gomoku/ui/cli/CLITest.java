/* package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.fail;

public class CLITest {

    @Test
    void init() {
        CLIMainViewmodel cliMainViewmodel = CLIMain.cliMainViewmodel;
        StartViewmodel startViewmodel = new StartViewmodel(cliMainViewmodel);

        startViewmodel.setPlayer1CPU(true);
        startViewmodel.setPlayer2CPU(true);
        startViewmodel.setNumberOfGames("1");
        startViewmodel.setSelectedBoardSize("SMALL");
        setFieldWithReflection(startViewmodel, "player1Name", "Pippo");
        setFieldWithReflection(startViewmodel, "player2Name", "Ciccio");

        new CLIMainView(startViewmodel);
        startViewmodel.startMatch();

        doFakeInput("n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        cliMainViewmodel.startNewGame();
//
//        try {
//            Game currentGame = (Game)
//                    TestUtility.getFieldAlreadyMadeAccessible(AbstractMainViewmodel.class, "currentGame")
//                            .get(cliMainViewmodel);
//
//
//            @SuppressWarnings("unchecked")
//            ObservableProperty<Game.Status> gameStatus =(ObservableProperty<Game.Status>)
//                    TestUtility.getFieldAlreadyMadeAccessible(Game.class, "gameStatus")
//                            .get(currentGame);
//            System.out.println(gameStatus.getPropertyValue());
//
//
//
//
//            @SuppressWarnings("unchecked")
//            ObservableProperty<Player> currentPlayer =(ObservableProperty<Player>)
//                    TestUtility.getFieldAlreadyMadeAccessible(Game.class, "currentPlayer")
//                    .get(currentGame);
////            System.out.println(currentPlayer.getPropertyValue());
////            currentPlayer.setPropertyValueAndFireIfPropertyChange(new CPUPlayer("Ciccio"));
//
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            fail(e);
//        }

//        System.out.println(cliMainViewmodel.getCurrentPlayer());
//        System.out.println(cliMainViewmodel.getCurrentBoardAsString());
    }

    private void setFieldWithReflection(Object object, String property, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(property);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }
//
//    private void setFieldOfObservableWithReflection(Object object, String property, Object value) {
//        try {
//            Field field = object.getClass().getDeclaredField(property);
//            field.setAccessible(true);
//            //noinspection unchecked
//            ObservableProperty<Object> op = (ObservableProperty<Object>) field.get(object);
//            op.setPropertyValueAndFireIfPropertyChange(value);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            fail(e);
//        }
//    }

    private void doFakeInput(String insertedInput) {
//        InputStream sysInBackup = System.in; // backup System.in to restore it later
        ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(insertedInput.getBytes());
        System.setIn(fakeStdIn);
//        System.setIn(sysInBackup);
    }

}
 */
