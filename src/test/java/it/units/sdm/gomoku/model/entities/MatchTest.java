package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import it.units.sdm.gomoku.model.entities.player.FakePlayer;
import it.units.sdm.gomoku.model.exceptions.GameAlreadyStartedException;
import it.units.sdm.gomoku.model.exceptions.GameNotEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchNotEndedException;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.utils.TestUtility;
import it.units.sdm.gomoku.utils.Utility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    private static void disputeNGamesOfMatchAndMakeGivenPlayerToWinAllTheGamesInMatch(
            int nGamesToDispute, @NotNull final Player winner, @NotNull final Match match) {

        assert nGamesToDispute <= SAMPLE_NUMBER_OF_GAMES;
        IntStream.range(0, nGamesToDispute).sequential().forEach(i -> {
            try {
                Game currentGame = match.initializeNewGame();
                currentGame.start();
                GameTestUtility.disputeGameAndMakeThePlayerToWin(currentGame, winner);
            } catch (GameAlreadyStartedException | MatchEndedException | GameNotEndedException e) {
                fail(e);
            }
        });
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

    private static Game initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(
            int numberOfGames, @NotNull final Match match) {

        assert numberOfGames <= SAMPLE_NUMBER_OF_GAMES;
        AtomicReference<Game> currentGame = new AtomicReference<>();
        IntStream.range(0, numberOfGames).sequential().forEach(i -> {
            try {
                currentGame.set(match.initializeNewGame());
                currentGame.get().start();
                GameTestUtility.disputeGameAndDraw(currentGame.get());
            } catch (GameAlreadyStartedException | MatchEndedException | GameNotEndedException e) {
                fail(e);
            }
        });

        return currentGame.get();
    }

    private static void makeGivenPlayerToWinNGamesInMatchAndTheOtherPlayerToWinTheRemainingGames(
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

    @Test
    void dontConsiderMatchEndedImmediatelyAfterItsCreation() {
        assertFalse(match.isEnded());
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
        assertEquals(SAMPLE_NUMBER_OF_GAMES, match.getTotalNumberOfGames());
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
        makeGivenPlayerToWinNGamesInMatchAndTheOtherPlayerToWinTheRemainingGames(winnerPlayer, loserPlayer, numberOfGameWon, match);
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
        makeGivenPlayerToWinNGamesInMatchAndTheOtherPlayerToWinTheRemainingGames(SAMPLE_PLAYER_1, SAMPLE_PLAYER_2, numberOfGamesWonByFirstPlayer, match);
        Map<Player, NonNegativeInteger> expectedScore;
        {
            expectedScore = new HashMap<>(2);
            expectedScore.put(SAMPLE_PLAYER_1, new NonNegativeInteger(numberOfGamesWonByFirstPlayer));
            expectedScore.put(SAMPLE_PLAYER_2, new NonNegativeInteger(match.getTotalNumberOfGames() - numberOfGamesWonByFirstPlayer));
        }
        assertEquals(expectedScore, match.getScore());
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testGetScoreWithDrawOfGames(int numberOfGamesWonByFirstPlayer) {
        initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(numberOfGamesWonByFirstPlayer, match);
        final int SCORE_OF_EACH_PLAYER_IF_GAME_ENDS_WITH_A_DRAW = 0;
        Map<Player, NonNegativeInteger> expectedScore;
        {
            expectedScore = new HashMap<>(2);
            expectedScore.put(SAMPLE_PLAYER_1, new NonNegativeInteger(SCORE_OF_EACH_PLAYER_IF_GAME_ENDS_WITH_A_DRAW));
            expectedScore.put(SAMPLE_PLAYER_2, new NonNegativeInteger(SCORE_OF_EACH_PLAYER_IF_GAME_ENDS_WITH_A_DRAW));
        }
        assertEquals(expectedScore, match.getScore());
    }
    //endregion test scores

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void invertColorsOfPlayersWhenNewGameIsCreated(int numberOfCurrentGame) {

        disputeNGamesOfMatchAndMakeGivenPlayerToWinAllTheGamesInMatch(numberOfCurrentGame + 1, SAMPLE_PLAYER_1, match);

        if (Utility.isEvenNumber(numberOfCurrentGame)) {
            assertEquals(match.getCurrentBlackPlayer(), SAMPLE_PLAYER_1);
        } else {
            assertEquals(match.getCurrentBlackPlayer(), SAMPLE_PLAYER_2);
        }
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testThereAreExactlyTheSameTwoPlayersForTheEntireMatch(int numberOfCurrentGame) {
        disputeNGamesOfMatchAndMakeGivenPlayerToWinAllTheGamesInMatch(numberOfCurrentGame + 1, SAMPLE_PLAYER_1, match);
        if (match.getCurrentBlackPlayer().equals(SAMPLE_PLAYER_1)) {
            assertEquals(SAMPLE_PLAYER_2, match.getCurrentWhitePlayer());
        } else if (match.getCurrentWhitePlayer().equals(SAMPLE_PLAYER_1)) {
            assertEquals(SAMPLE_PLAYER_2, match.getCurrentBlackPlayer());
        } else {
            fail("Unknown player");
        }
    }

    @Test
    void testIncrementTotalNumberOfGames() {
        int initialValue = match.getTotalNumberOfGames();
        match.incrementTotalNumberOfGames();
        assertEquals(initialValue + 1, match.getTotalNumberOfGames());
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void addGameToGameListWhenNewGameInitialized(int gameNumber) throws NoSuchFieldException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        List<Game> gameList = (List<Game>) TestUtility.getFieldValue("gameList", match);
        currentGame = initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(gameNumber + 1, match);
        assert gameList != null;
        assertEquals(currentGame, gameList.get(gameNumber));
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

    @Test
    void dontSetWinnerOfMatchIfMatchNotEnded() {
        try {
            match.getWinner();
            fail("Match not ended");
        } catch (Exception e) {
            assertTrue(e instanceof MatchNotEndedException);
        }
    }

    @Test
    void dontSetWinnerOfMatchIfMatchEndedWithADraw() throws MatchNotEndedException {
        for (int i = 0; i < SAMPLE_NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        assertNull(match.getWinner());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void setWinnerOfMatchThePlayerWhoWonMoreGames(int samplePlayerIndex) throws MatchNotEndedException {

        Player winnerOfMatch = samplePlayerIndex == 1 ? SAMPLE_PLAYER_1 : SAMPLE_PLAYER_2;
        Player loserOfMatch = winnerOfMatch.equals(SAMPLE_PLAYER_1) ? SAMPLE_PLAYER_2 : SAMPLE_PLAYER_1;

        int numberOfGamesToWinForAPlayerToBeTheWinnerOfMatch = match.getTotalNumberOfGames() / 2 + 1;
        makeGivenPlayerToWinNGamesInMatchAndTheOtherPlayerToWinTheRemainingGames(winnerOfMatch, loserOfMatch, numberOfGamesToWinForAPlayerToBeTheWinnerOfMatch, match);
        assertEquals(winnerOfMatch, match.getWinner());
    }

    @Test
    void endMatchAfterLastGame() {
        initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(SAMPLE_NUMBER_OF_GAMES, match);
        assertTrue(match.isEnded());
    }

    @ParameterizedTest
    @MethodSource("getIntStreamFrom0IncludedToTotalNumberOfGamesExcluded")
    void testIfMatchIsEnded(int gameIndex) {
        int numberOfGamesToDispute = gameIndex + 1;
        initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(numberOfGamesToDispute, match);
        assertEquals(numberOfGamesToDispute == SAMPLE_NUMBER_OF_GAMES, match.isEnded());
    }

    @Test
    void testIfMatchIsEndedAfterAddExtraGame() {
        initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(SAMPLE_NUMBER_OF_GAMES, match);
        assert match.isEnded();
        match.incrementTotalNumberOfGames();
        assertFalse(match.isEnded());
    }


    @Test
    void testIfMatchEndedWithADraw() throws MatchNotEndedException {
        int numberOfGamesThatEachPlayerHasToWinToHaveADraw = SAMPLE_NUMBER_OF_GAMES / 2;
        makeGivenPlayerToWinNGamesInMatchAndTheOtherPlayerToWinTheRemainingGames(
                match.getCurrentBlackPlayer(), match.getCurrentWhitePlayer(),
                numberOfGamesThatEachPlayerHasToWinToHaveADraw, match);
        if (!Utility.isEvenNumber(SAMPLE_NUMBER_OF_GAMES)) {
            Runnable changeLastGameToEndTheMatchWithADraw = () -> {
                try {
                    List<Game> gameList = null;
                    //noinspection unchecked
                    gameList = (List<Game>) TestUtility.getFieldValue("gameList", match);
                    assert SAMPLE_NUMBER_OF_GAMES > 0;
                    assert gameList != null;
                    gameList.remove(gameList.size() - 1);
                    initializeAndDisputeNGameAndEndThemWithDrawAndGetLastInitializedGame(1, match);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    fail(e);
                }
            };
            changeLastGameToEndTheMatchWithADraw.run();
        }
        assertTrue(match.isADraw());
    }

    @Test
    void isNotADraw() throws MatchNotEndedException {
//        startGameAndPlayerWin(SAMPLE_PLAYER_1);
//        for (int i = 1; i < SAMPLE_NUMBER_OF_GAMES; i++) {
//            startGameAndDraw();
//        }
//        assertFalse(match.isADraw());
    }

}