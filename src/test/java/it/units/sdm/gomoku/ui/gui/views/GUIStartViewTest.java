package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.ThrowingRunnable;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.exceptions.SceneControllerNotInstantiatedException;
import it.units.sdm.gomoku.ui.gui.GUIMain;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.support.BoardSizes;
import it.units.sdm.gomoku.ui.support.Setup;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.ui.StartViewmodel.boardSizes;
import static it.units.sdm.gomoku.ui.support.Setup.getSetupFromMatch;
import static org.junit.jupiter.api.Assertions.*;

class GUIStartViewTest {

    // TODO : missing tests + very long class code smell

    private GUIStartView guiStartView;
    private StartViewmodel guiStartViewmodel;
    private static final AtomicBoolean isJavaFxRunning = new AtomicBoolean(false);
    private static final Logger loggerThisTest = Logger.getLogger(GUIStartView.class.getCanonicalName());

    @BeforeEach
    void setUp() {
        try {
            setUpJavaFXRuntime();
            Method sceneCreatorMethod = SceneController.class.getDeclaredMethod(
                    "createScene", FXMLLoader.class, double.class, double.class);
            sceneCreatorMethod.setAccessible(true);
            FXMLLoader fxmlLoader = new FXMLLoader(GUIStartView.class.getResource(GUIMain.START_VIEW_FXML_FILE_NAME));
            sceneCreatorMethod.invoke(null, fxmlLoader, 0, 0);
            guiStartView = fxmlLoader.getController();
            guiStartViewmodel = guiStartView.getViewmodelAssociatedWithView();
        } catch (InterruptedException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail(e);
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            tearDownJavaFXRuntime();
        } catch (InterruptedException e) {
            fail(e);
        }
    }

//    @Test
//    void startMatchButtonOnMouseClicked() {
//        // TODO: all input fields must be valid and already set in viewmodel and view must change
//    }
//
//    @Test
//    void initialize() {
//        // TODO : assert the correct view is shown with correct input field values and same values saved in Viewmodel
//    }

    private static void setUpJavaFXRuntime() throws InterruptedException {
        if (!isJavaFxRunning.get()) {
            isJavaFxRunning.set(true);
            Platform.startup(() -> {
                while (isJavaFxRunning.get()) {
                }
            });
        }
    }

    private static void tearDownJavaFXRuntime() throws InterruptedException {
        isJavaFxRunning.set(false);
        Platform.setImplicitExit(false);
    }

    @NotNull
    private static Stream<Arguments> pairOfPositiveIntegerSupplier() {
        Set<Integer> ints = Objects.requireNonNull(readIntegersFromCSV());
        return ints.stream().unordered().parallel()
                .filter(NonNegativeInteger::isValid)
                .flatMap(i ->
                        ints.stream().unordered().parallel()
                                .filter(PositiveInteger::isValid)
                                .map(j -> Arguments.of(i, j)));
    }

    @NotNull
    private static Stream<Arguments> pairOfIntegerSupplier() {
        Set<Integer> ints = Objects.requireNonNull(readIntegersFromCSV());
        return ints.stream().unordered().parallel()
                .flatMap(i -> ints.stream().unordered().parallel().map(j -> Arguments.of(i, j)));
    }

    @Nullable
    private static Set<Integer> readIntegersFromCSV() {
        final String COMMENT_STARTER_CHARACTER = "#";
        final String VALUE_SEPARATOR = ",";
        Predicate<String> nonCommentedLineInCSVFile = aLine -> !aLine.trim().startsWith(COMMENT_STARTER_CHARACTER);
        try {
            return Files.readAllLines(Path.of(Objects.requireNonNull(
                            GUIStartView.class.getResource(EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)).toURI()))
                    .stream().unordered().parallel()
                    .filter(nonCommentedLineInCSVFile)
                    .flatMap(aLine -> Arrays.stream(aLine.split(VALUE_SEPARATOR)).unordered().parallel())
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet());
        } catch (IOException | URISyntaxException e) {
            loggerThisTest.log(Level.SEVERE, "Exception generated in supplier method: null returned", e);
            return null;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "One, One, player1NameTextField, player1Name",
            "One, Two, player1NameTextField, player1Name",
            "Two, One, player2NameTextField, player2Name",
            "Two, Two, player2NameTextField, player2Name"
    })
    void updatePlayerNameInViewShouldAutomaticallyUpdateFieldInViewmodel(
            String oldPlayerName, String newPlayerName, String textfieldNameInView, String fieldNameInViewmodel) {
        try {
            TextField playerNameTextField = getTextField(textfieldNameInView);
            assertSynchronizationBetweenViewAndViewmodel(
                    oldPlayerName, newPlayerName, fieldNameInViewmodel,
                    playerNameTextField.textProperty()::set);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("pairOfIntegerSupplier")
    void numberOfGamesInViewShouldAcceptOnlyNonNegativeInteger(
            int oldNumberOfGamesAlreadySet, int newNumberOfGamesInsertedByUser) {
        final String textfieldNameInView = "numberOfGamesTextField";
        final String fieldNameInViewmodel = "numberOfGames";
        try {
            TextField numberOfGamesTextField = getTextField(textfieldNameInView);
            setOldValueInViewmodelAndThenSetNewValueInView(
                    String.valueOf(oldNumberOfGamesAlreadySet), String.valueOf(newNumberOfGamesInsertedByUser),
                    fieldNameInViewmodel,
                    numberOfGamesTextField.textProperty()::setValue);
            if (oldNumberOfGamesAlreadySet >= 0) {
                if (newNumberOfGamesInsertedByUser > Integer.MIN_VALUE) {
                    assertEquals(
                            String.valueOf(Math.abs(newNumberOfGamesInsertedByUser)),
                            TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel));
                } else {
                    loggerThisTest.warning(
                            "Test considered passed due to inconsistent input value:" +
                                    " OVERFLOW: abs value of oldValue causes overflow:" +
                                    " oldValue=" + oldNumberOfGamesAlreadySet +
                                    ", newValue=" + newNumberOfGamesInsertedByUser);
                }
            } else {
                loggerThisTest.warning(
                        "Test considered passed due to inconsistent input value:" +
                                " Negative old value (should never happen):" +
                                " oldValue=" + oldNumberOfGamesAlreadySet +
                                ", newValue=" + newNumberOfGamesInsertedByUser);
                // TODO : inconsistent state (old value should never be negative)
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("pairOfPositiveIntegerSupplier")
    void updatingNumberOfGamesInViewShouldAutomaticallyUpdateFieldInViewmodel(
            int oldNumberOfGames, int newNumberOfGames) {
        final String textfieldNameInView = "numberOfGamesTextField";
        final String fieldNameInViewmodel = "numberOfGames";
        try {
            TextField numberOfGamesTextField = getTextField(textfieldNameInView);
            assertSynchronizationBetweenViewAndViewmodel(
                    String.valueOf(oldNumberOfGames), String.valueOf(newNumberOfGames), fieldNameInViewmodel,
                    numberOfGamesTextField.textProperty()::set);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    private TextField getTextField(String textfieldNameInView) throws IllegalAccessException, NoSuchFieldException {
        return (TextField) TestUtility
                .getFieldAlreadyMadeAccessible(guiStartView.getClass(), textfieldNameInView)
                .get(guiStartView);
    }

    @ParameterizedTest
    @CsvSource({
            "true, true, player1CPUCheckBox, player1CPU",
            "true, false, player1CPUCheckBox, player1CPU",
            "false, true, player1CPUCheckBox, player1CPU",
            "false, false, player1CPUCheckBox, player1CPU",
            "true, true, player2CPUCheckBox, player2CPU",       // TODO : refactor with @MethodSource?
            "true, false, player2CPUCheckBox, player2CPU",
            "false, true, player2CPUCheckBox, player2CPU",
            "false, false, player2CPUCheckBox, player2CPU"
    })
    void updatePlayerIsCPUCheckboxInViewShouldAutomaticallyUpdateFieldInViewmodel(
            boolean wasCPUBeforeUpdate, boolean isCPUAfterUpdate,
            String checkboxNameInView, String fieldNameInViewmodel) {
        try {
            CheckBox isCPUSelectedCheckBox = getIsCPUCheckBox(checkboxNameInView);
            assertSynchronizationBetweenViewAndViewmodel(
                    wasCPUBeforeUpdate, isCPUAfterUpdate, fieldNameInViewmodel, isCPUSelectedCheckBox::setSelected);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    private static Stream<Arguments> boardSizeNewAndOldValuesSupplier() {
        return Arrays.stream(BoardSizes.values())
                .flatMap(boardSizeOldValue -> Arrays.stream(BoardSizes.values())
                        .map(boardSizeNewValue -> Arguments.of(boardSizeOldValue, boardSizeNewValue)));
    }

    private <T> void assertSynchronizationBetweenViewAndViewmodel(
            @Nullable final T oldValue, @Nullable final T newValue,
            @NotNull final String fieldNameInViewmodel, @NotNull final Consumer<T> propertyValueSetterInView)
            throws NoSuchFieldException, IllegalAccessException {
        setOldValueInViewmodelAndThenSetNewValueInView(oldValue, newValue, fieldNameInViewmodel, propertyValueSetterInView);
        assertEquals(TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel), newValue);
    }

    @NotNull
    private CheckBox getIsCPUCheckBox(String checkboxNameInView) throws IllegalAccessException, NoSuchFieldException {
        return (CheckBox) TestUtility
                .getFieldAlreadyMadeAccessible(guiStartView.getClass(), checkboxNameInView)
                .get(guiStartView);
    }

    private <T> void setOldValueInViewmodelAndThenSetNewValueInView(
            @Nullable T oldValue, @Nullable T newValue, @NotNull String fieldNameInViewmodel,
            @NotNull Consumer<T> propertyValueSetterInView)
            throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue(Objects.requireNonNull(fieldNameInViewmodel), oldValue, Objects.requireNonNull(guiStartViewmodel));
        assert TestUtility.getFieldValue(fieldNameInViewmodel, guiStartViewmodel).equals(oldValue);
        Objects.requireNonNull(propertyValueSetterInView).accept(oldValue); // set old state before firing property change
        propertyValueSetterInView.accept(newValue);
    }

    @ParameterizedTest
    @MethodSource("boardSizeNewAndOldValuesSupplier")
    void updatingBoardSizeInViewShouldAutomaticallyUpdateFieldInViewmodel(
            BoardSizes oldBoardSize, BoardSizes newBoardSize) {
        final String fieldNameInViewmodel = "selectedBoardSize";
        try {
            ChoiceBox<String> boardSizeChoiceBox = getBoardSizeChoiceBox();
            assertSynchronizationBetweenViewAndViewmodel(
                    oldBoardSize.getExposedValueOf(), newBoardSize.getExposedValueOf(), fieldNameInViewmodel, boardSizeChoiceBox::setValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @NotNull
    private ChoiceBox<String> getBoardSizeChoiceBox() throws IllegalAccessException, NoSuchFieldException {
        final String choiceboxNameInView = "boardSizeChoiceBox";
        @SuppressWarnings("unchecked")  // this choiceBox has string values // TODO : can be generalized?
        ChoiceBox<String> boardSizeChoiceBox =
                (ChoiceBox<String>) TestUtility
                        .getFieldAlreadyMadeAccessible(guiStartView.getClass(), choiceboxNameInView)
                        .get(guiStartView);
        return boardSizeChoiceBox;
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.ui.UIUtility#setupsSupplierAndFlagIfValid")
    void createMatchWhenClickButtonIfValidFields(Setup setup, boolean validSetup) {
        AtomicReference<ReflectiveOperationException> eventuallyThrownException = new AtomicReference<>();  // TODO: create interface "SupplierThatThrows" to avoid this mechanism and replace all occurrences of this "pattern"
        eventuallyThrownException.set(null);
        ThrowingRunnable<ReflectiveOperationException> throwIfExceptionWasThrown = () -> {
            if (eventuallyThrownException.get() != null) {
                throw eventuallyThrownException.get();
            }
        };
        Function<MainViewmodel, Match> getCurrentMatchOrNullIfExceptionThrown = mainViewmodel -> {
            try {
                return (Match) TestUtility.getFieldValue("match", mainViewmodel);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                eventuallyThrownException.set(e);
                return null;
            }
        };
        try {
            MainViewmodel mainViewmodel = (MainViewmodel) TestUtility
                    .getFieldValue("mainViewmodel", guiStartViewmodel);
            Match matchBeforeUserConfirmFieldsInStartView = getCurrentMatchOrNullIfExceptionThrown.apply(mainViewmodel);
            throwIfExceptionWasThrown.run();
            assert matchBeforeUserConfirmFieldsInStartView == null;
            setFieldsInViewFromSetup(setup);
            try {
                guiStartView.startMatchButtonOnMouseClicked(null);// whatever ("null" or "Foo" included) parameter is ok  // TODO: re-see this
            } catch (SceneControllerNotInstantiatedException ignored) {
            }
            Match matchAfterUserConfirmFieldsInStartView = getCurrentMatchOrNullIfExceptionThrown.apply(mainViewmodel);
            throwIfExceptionWasThrown.run();
            if (validSetup) {
                assertEquals(setup, getSetupFromMatch(matchAfterUserConfirmFieldsInStartView));

                // TODO : move in separate test: after starting new game, gamelist has exactly 1 (the first just started) game
                assertEquals(1, ((List<?>) Objects.requireNonNull(
                        TestUtility.getFieldValue("gameList", matchAfterUserConfirmFieldsInStartView))).size());
            } else {
                assertNull(matchAfterUserConfirmFieldsInStartView);
            }
        } catch (ReflectiveOperationException e) {
            fail(e);
        }

    }

    private void setFieldsInViewFromSetup(Setup setup) throws NoSuchFieldException, IllegalAccessException {
        // TODO : refactor may needed
        getTextField("player1NameTextField").textProperty().set(setup.player1().getName());
        getTextField("player2NameTextField").textProperty().set(setup.player2().getName());
        getIsCPUCheckBox("player1CPUCheckBox").setSelected(setup.player1() instanceof CPUPlayer);
        getIsCPUCheckBox("player2CPUCheckBox").setSelected(setup.player2() instanceof CPUPlayer);
        getBoardSizeChoiceBox().setValue(boardSizes.get(boardSizes.size() / 2));
        getTextField("numberOfGamesTextField").textProperty().set(setup.numberOfGames().toString());
    }

}