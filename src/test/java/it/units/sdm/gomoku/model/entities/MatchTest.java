package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import it.units.sdm.gomoku.model.entities.player.FakePlayer;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private static final Player SAMPLE_PLAYER_1 = new FakePlayer("A");
    private static final Player SAMPLE_PLAYER_2 = new FakePlayer("B");
    private static final int SAMPLE_NUMBER_OF_GAMES = 5;
    private static final PositiveInteger SAMPLE_BOARD_SIZE = new PositiveInteger(BoardSizes.NORMAL.getBoardSize());

    private Match match;
    private Game currentGame;

    public static Stream<Arguments> getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded() {
        return IntStream.range(0, SAMPLE_NUMBER_OF_GAMES).mapToObj(Arguments::of);
    }

    //region Support Methods    // todo : TRY NOT TO USE THIS REGION
    private void assertCpusScore(int n1, int n2) {
        assertEquals(n1, match.getScore().get(SAMPLE_PLAYER_1).intValue());
        assertEquals(n2, match.getScore().get(SAMPLE_PLAYER_2).intValue());
    }

    private void startNewGameComplete() {
        try {
            currentGame = match.initializeNewGame();
            currentGame.start();
        } catch (MatchEndedException | GameNotEndedException | GameAlreadyStartedException e) {
            fail(e);
        }
    }

    private void startGameAndPlayerWin(Player player) {
        startNewGameComplete();
        GameTestUtility.disputeGameAndMakeThePlayerToWin(currentGame, player);
    }

    private void startGameAndDraw() {
        startNewGameComplete();
        GameTestUtility.disputeGameAndDraw(currentGame);
    }
    //endregion Support Methods //

    @BeforeEach
    void setup() {
        match = createNewMatchWithDefaultParameterAndGet();
    }

    @NotNull
    private Match createNewMatchWithDefaultParameterAndGet() {
        return new Match(SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, new PositiveInteger(SAMPLE_NUMBER_OF_GAMES), SAMPLE_BOARD_SIZE);
    }

    //region test constructors
    @Test
    void createNewInstanceWithAllFieldsNotNull() {
        List<String> namesOfFieldsWhichMayBeNull = new ArrayList<>();
        int numberOfNullFieldsAfterConstructionNotInExclusionList = (int)
                TestUtility.getNumberOfNullFieldsOfObjectWhichNameIsNotInList(namesOfFieldsWhichMayBeNull, match);
        int numberOfFieldsExpectedToBeNullAfterCreation = 0;
        assertEquals(numberOfFieldsExpectedToBeNullAfterCreation, numberOfNullFieldsAfterConstructionNotInExclusionList);
    }

    @Test
    void createNewInstanceFromSetup() {
        Setup setup = new Setup(SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, new PositiveInteger(SAMPLE_NUMBER_OF_GAMES), SAMPLE_BOARD_SIZE);
        Match match = new Match(SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, new PositiveInteger(SAMPLE_NUMBER_OF_GAMES), SAMPLE_BOARD_SIZE);
        Match matchFromSetup = new Match(setup);

        assertEquals(match, matchFromSetup);
    }
    //endregion

    //region test equals
    @Test
    void equalIfSameObject() {
        assertEquals(match, match);
    }

    @Test
    void notEqualIfDifferentClasses() {
        //noinspection RedundantCast    // used to test equals
        assertNotEquals((Object) match, "");
    }

    @SuppressWarnings("unchecked")
    @Test
    void notEqualIfDifferentGameList() throws NoSuchFieldException, IllegalAccessException {
        match = createNewMatchWithDefaultParameterAndGet();
        Match matchWithDifferentGameList;
        {
            matchWithDifferentGameList = createNewMatchWithDefaultParameterAndGet();
            ((List<Game>) Objects.requireNonNull(TestUtility.getFieldValue("gameList", matchWithDifferentGameList)))
                    .add(GameTestUtility.createNewGameWithDefaultParams());
        }
        assertNotEquals(match, matchWithDifferentGameList);
    }

    @Test
    void notEqualIfDifferentNumberOfGames() {
        match = createNewMatchWithDefaultParameterAndGet();
        Match matchWithDifferentDifferentNumberOfGames;
        {
            matchWithDifferentDifferentNumberOfGames = createNewMatchWithDefaultParameterAndGet();
            matchWithDifferentDifferentNumberOfGames.incrementTotalNumberOfGames();
        }
        assertNotEquals(match, matchWithDifferentDifferentNumberOfGames);
    }

    @Test
    void notEqualIfDifferentCurrentBlackPlayer() throws NoSuchFieldException, IllegalAccessException {
        match = createNewMatchWithDefaultParameterAndGet();
        Match matchWithDifferentDifferentCurrentBlackPlayer;
        {
            matchWithDifferentDifferentCurrentBlackPlayer = createNewMatchWithDefaultParameterAndGet();
            TestUtility.setFieldValue(
                    "currentBlackPlayer", new FakePlayer("Different black player"),
                    matchWithDifferentDifferentCurrentBlackPlayer);
        }
        assertNotEquals(match, matchWithDifferentDifferentCurrentBlackPlayer);
    }

    @Test
    void notEqualIfDifferentCurrentWhitePlayer() throws NoSuchFieldException, IllegalAccessException {
        match = createNewMatchWithDefaultParameterAndGet();
        Match matchWithDifferentDifferentCurrentWhitePlayer;
        {
            matchWithDifferentDifferentCurrentWhitePlayer = createNewMatchWithDefaultParameterAndGet();
            TestUtility.setFieldValue(
                    "currentWhitePlayer", new FakePlayer("Different white player"),
                    matchWithDifferentDifferentCurrentWhitePlayer);
        }
        assertNotEquals(match, matchWithDifferentDifferentCurrentWhitePlayer);
    }

    @Test
    void equalIfNotSameButWithEqualFieldValues() {
        Match match = createNewMatchWithDefaultParameterAndGet();
        Match anotherMatch = createNewMatchWithDefaultParameterAndGet();
        assertEquals(match, anotherMatch);
    }

    @Test
    void sameHashCodeIfEqual() {
        Match match = createNewMatchWithDefaultParameterAndGet();
        Match anotherMatch = createNewMatchWithDefaultParameterAndGet();
        assert match.equals(anotherMatch);
        assertEquals(match.hashCode(), anotherMatch.hashCode());
    }

    @Test
    void notEqualIfDifferentHashCode() {
        Match match = createNewMatchWithDefaultParameterAndGet();
        Match anotherMatchWithDifferentHashCode;
        {
            anotherMatchWithDifferentHashCode = createNewMatchWithDefaultParameterAndGet();
            anotherMatchWithDifferentHashCode.incrementTotalNumberOfGames();
        }
        assert match.hashCode() != anotherMatchWithDifferentHashCode.hashCode();
        assertNotEquals(match, anotherMatchWithDifferentHashCode);
    }
    //endregion test equals

    //region test getters
    @Test
    void testGetNumberOfGames() {
        assertEquals(SAMPLE_NUMBER_OF_GAMES, match.getNumberOfGames());
    }

    @Test
    void testGetCurrentBlackPlayer() {
        assertEquals(SAMPLE_PLAYER_1, match.getCurrentBlackPlayer());
    }

    @Test
    void testGetCurrentWhitePlayer() {
        assertEquals(SAMPLE_PLAYER_2, match.getCurrentWhitePlayer());
    }

    @Test
    void testGetCurrentGame() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, MatchEndedException, GameNotEndedException {
        currentGame = match.initializeNewGame();
        assertEquals(currentGame, TestUtility.invokeMethodOnObject(match, "getCurrentGame"));
    }
    //endregion test getters

    //region test scores
    private static void testGetScoreOfPlayer(
            int numberOfGameWon, @NotNull final Player winnerPlayer, @NotNull final Player loserPlayer, @NotNull final Match match)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        assert numberOfGameWon <= SAMPLE_NUMBER_OF_GAMES;
        makeGivenPlayerToWinNGamesInMatch(winnerPlayer, loserPlayer, numberOfGameWon, match);
        Method scoreOfPlayerGetter = match.getClass().getDeclaredMethod("getScoreOfPlayer", Player.class);
        scoreOfPlayerGetter.setAccessible(true);
        assertEquals(numberOfGameWon, ((NonNegativeInteger) scoreOfPlayerGetter.invoke(match, winnerPlayer)).intValue());
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testGetSCoreOfFirstPlayer(int numberOfGameWon) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        testGetScoreOfPlayer(numberOfGameWon, SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, match);
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testGetSCoreOfSecondPlayer(int numberOfGameWon) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        testGetScoreOfPlayer(numberOfGameWon, SAMPLE_PLAYER_2, SAMPLE_PLAYER_1, match);
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testGetScore(int numberOfGamesWonByFirstPlayer) {
        makeGivenPlayerToWinNGamesInMatch(SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, numberOfGamesWonByFirstPlayer, match);
        Map<Player, NonNegativeInteger> expectedScore;
        {
            expectedScore = new HashMap<>(2);
            expectedScore.put(SAMPLE_PLAYER_1, new NonNegativeInteger(numberOfGamesWonByFirstPlayer));
            expectedScore.put(SAMPLE_PLAYER_2, new NonNegativeInteger(match.getNumberOfGames() - numberOfGamesWonByFirstPlayer));
        }
        assertEquals(expectedScore, match.getScore());
    }
    //endregion test scores

    private static void makeGivenPlayerToWinNGamesInMatch(
            @NotNull final Player playerWhoHasToWin, @NotNull final Player playerWhoHasToLose,
            int numberOfGameWon, @NotNull final Match match) {

        assert numberOfGameWon <= SAMPLE_NUMBER_OF_GAMES;
        IntStream.range(0, SAMPLE_NUMBER_OF_GAMES).sequential().forEach(i -> {
            try {
                Game currentGame = match.initializeNewGame();
                currentGame.start();
                if (i < numberOfGameWon) {
                    GameTestUtility.disputeGameAndMakeThePlayerToWin(currentGame, playerWhoHasToWin);
                } else {
                    GameTestUtility.disputeGameAndMakeThePlayerToWin(currentGame, playerWhoHasToLose);
                }
            } catch (GameAlreadyStartedException | MatchEndedException | GameNotEndedException e) {
                fail(e);
            }
        });
    }

    @Test
    void addFirstGameOfTheMatchToGameList() throws MatchEndedException, NoSuchFieldException, IllegalAccessException, GameNotEndedException {
        currentGame = match.initializeNewGame();
        Field fieldGameList = match.getClass().getDeclaredField("gameList");
        fieldGameList.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Game> gameList = (List<Game>) fieldGameList.get(match);
        assertEquals(currentGame, gameList.get(0));
    }

    @Test
    void setFirstPlayerAsTheBlackOneInTheFirstGame() throws MatchEndedException, GameNotEndedException {
        match.initializeNewGame();
        assertEquals(SAMPLE_PLAYER_1, match.getCurrentBlackPlayer());
    }

    @Test
    void setFirstPlayerAsTheWhiteOneInTheSecondGame()
            throws MatchEndedException, GameNotEndedException, GameAlreadyStartedException {

        currentGame = match.initializeNewGame();
        currentGame.start();
        GameTestUtility.disputeGameAndDraw(currentGame);
        match.initializeNewGame();
        assertEquals(SAMPLE_PLAYER_1, match.getCurrentWhitePlayer());
    }

//    @Test
//    void maxNumberOfGamesException() {//TODO: substute with not game ended exception
//        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
//            startNewGameComplete();
//            GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
//        }
//
//        try {
//            match.initializeNewGame();
//            fail("Is over the number of games!");
//        } catch (MatchEndedException | GameNotEndedException e) {
//            fail(e);
//        }
//    }
//
//    @Test
//    void getScoreBeforeStart() {
//        assertCpusScore(0, 0);
//    }
//
//    @Test
//    void getScoreAfterStart() throws MatchEndedException, GameNotEndedException {
//        match.initializeNewGame();
//        assertCpusScore(0, 0);
//    }
//
//    @Test
//    void getScoreAfterPlayer1Win() {
//        startGameAndPlayerWin(SAMPLE_PLAYER_1);
//        assertCpusScore(1, 0);
//    }
//
//    @Test
//    void getScoreAfterPlayer2Win() {
//        startGameAndPlayerWin(SAMPLE_PLAYER_2);
//        assertCpusScore(0, 1);
//    }
//
//    @Test
//    void getScoreAfterADrawGame() {
//        startGameAndDraw();
//        assertCpusScore(0, 0);
//    }

    @Test
    void getWinnerIfMatchNotEnded() {
        try {
            match.getWinner();
            fail("Match not ended");
        } catch (MatchNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerWithADraw() throws MatchNotEndedException {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        assertNull(match.getWinner());
    }

    @Test
    void getWinnerWithPlayer1Win() throws MatchNotEndedException {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndPlayerWin(SAMPLE_PLAYER_1);
        }
        assertEquals(SAMPLE_PLAYER_1, match.getWinner());
    }

    @Test
    void getWinnerWithPlayer2Win() throws MatchNotEndedException {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndPlayerWin(SAMPLE_PLAYER_2);
        }
        assertEquals(SAMPLE_PLAYER_2, match.getWinner());
    }


    @Test
    void isEndedAtStartMatch() {
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedAfterAGame() {
        if (SAMPLE_NUMBER_OF_GAMES != 1) {
            startGameAndDraw();
            assertFalse(match.isEnded());
        }
    }

    @Test
    void isEndedAfterStartLastGame() {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES - 1; i++) {
            startGameAndDraw();
        }
        startNewGameComplete();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedNormalFlow() throws GameNotStartedException {
        isEndedAfterStartLastGame();
        GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
        assertTrue(match.isEnded());
    }

    @Test
    void isEndedAfterAddExtraGame() throws GameNotStartedException {
        isEndedNormalFlow();
        match.incrementTotalNumberOfGames();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedAfterEndExtraGame() throws GameNotStartedException {
        isEndedAfterAddExtraGame();
        startGameAndDraw();
        assertTrue(match.isEnded());
    }

    @Test
    void isADrawMatchNotEnded() {
        try {
            match.isADraw();
            fail("Not throw MatchNotEndedException");
        } catch (MatchNotEndedException ignored) {
        }
    }

    @Test
    void isADraw() {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        try {
            assertTrue(match.isADraw());
        } catch (MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void isNotADraw() throws MatchNotEndedException {
        startGameAndPlayerWin(SAMPLE_PLAYER_1);
        for (int i = 1; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        assertFalse(match.isADraw());
    }

}