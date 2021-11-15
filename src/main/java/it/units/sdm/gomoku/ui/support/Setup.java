package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.actors.Player;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public record Setup(Player player1,
                    Player player2,
                    PositiveInteger numberOfGames,
                    PositiveInteger boardSize) {

    public Setup(@NotNull final Player player1,
                 @NotNull final Player player2,
                 @NotNull final PositiveInteger numberOfGames,
                 @NotNull final PositiveInteger boardSize) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
        this.boardSize = Objects.requireNonNull(boardSize);
    }

    @NotNull
    public static Setup getSetupFromMatch(@NotNull final Match match) throws NoSuchFieldException, IllegalAccessException {
        // TODO : test
        Objects.requireNonNull(match);
        return new Setup(
                match.getCurrentBlackPlayer(), match.getCurrentWhitePlayer(),
                new PositiveInteger(match.getNumberOfGames()),
                (PositiveInteger) Objects.requireNonNull(getFieldValue("boardSize", match)));
    }

    @Nullable
    public static <T> Object getFieldValue(@NotNull final String fieldName, @NotNull final T objectInstance)
            throws NoSuchFieldException, IllegalAccessException {   // TODO : method copied from class TestUtility in test directory
        return getFieldAlreadyMadeAccessible(
                Objects.requireNonNull(objectInstance).getClass(), Objects.requireNonNull(fieldName))
                .get(objectInstance);
    }

    @NotNull
    public static Field getFieldAlreadyMadeAccessible(@NotNull final Class<?> clazz,
                                                      @NotNull final String fieldName)
            throws NoSuchFieldException {    // TODO : method copied from class TestUtility in test directory
        Field field = Objects.requireNonNull(clazz)
                .getDeclaredField(Objects.requireNonNull(fieldName));
        field.setAccessible(true);
        return field;
    }

    @Override
    public boolean equals(Object o) {   // TODO: test
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setup setup = (Setup) o;
        if (!player1.equals(setup.player1)) return false;
        if (!player2.equals(setup.player2)) return false;
        if (!numberOfGames.equals(setup.numberOfGames)) return false;
        return boardSize.equals(setup.boardSize);
    }

    @Override
    public int hashCode() {    // TODO: test
        int result = player1.hashCode();
        result = 31 * result + player2.hashCode();
        result = 31 * result + numberOfGames.hashCode();
        result = 31 * result + boardSize.hashCode();
        return result;
    }
}