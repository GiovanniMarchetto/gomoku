package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    private final static int MAX_BUFFER_SIZE_USED_IN_TEST = 10000;
    private final static String actualBufferFieldNameInClass = "buffer";
    private final static String sizeFieldNameInClass = "size";

    private final static int ARBITRARY_CHOSEN_SIZE = 10;
    private final static int REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START = 10;
    private final static int REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED =
            2 * REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START;

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorValidateSize(int size) {
        if (size <= MAX_BUFFER_SIZE_USED_IN_TEST) {
            try {
                new Buffer<>(size);
                assertTrue(size > 0);
            } catch (Exception e) {
                assertTrue(size <= 0);
            }
        } else {
            Utility.getLoggerOfClass(getClass()).info(
                    "Parameter size=" + size + " is greater than " + MAX_BUFFER_SIZE_USED_IN_TEST + ", " +
                            "hence the test is considered passed");
        }
    }

    private static void fillBufferWithIntegers(@NotNull final Buffer<Integer> buffer)
            throws NoSuchFieldException, IllegalAccessException {
        //noinspection ConstantConditions
        IntStream.range(0, (int) TestUtility.getFieldValue(sizeFieldNameInClass, buffer))
                .boxed()
                .forEach(buffer::insert);
    }

    @SuppressWarnings("unchecked")  // cast inner buffer to list of correct type
    @Nullable
    private static <ElementType> List<ElementType> getActualBuffer(@NotNull final Buffer<ElementType> buffer) throws NoSuchFieldException, IllegalAccessException {
        return (List<ElementType>) TestUtility.getFieldValue(actualBufferFieldNameInClass, Objects.requireNonNull(buffer));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectSize(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue(sizeFieldNameInClass, buffer), size);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectlyInitializedBuffer(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue(actualBufferFieldNameInClass, buffer), new ArrayList<Integer>(size));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.model.custom_types.CoordinatesTest#nonNegativeIntegerPairAndValidFlagSupplier"/* whatever supplier of parameters is ok */)
    <ElementType> void insertOneElementWhenBufferIsEmpty(ElementType element) {
        final int ARBITRARY_CHOSEN_SIZE = 10;
        Buffer<ElementType> buffer = new Buffer<>(ARBITRARY_CHOSEN_SIZE);
        assert isBufferEmptyImmediatelyAfterCreation(buffer);
        buffer.insert(element);
        final int NUMBER_OF_INSERTED_ELEMENTS = 1;
        assertEquals(NUMBER_OF_INSERTED_ELEMENTS, buffer.getNumberOfElements());
    }

    private <ElementType> boolean isBufferEmptyImmediatelyAfterCreation(@NotNull final Buffer<ElementType> buffer) {
        return Objects.requireNonNull(buffer).getNumberOfElements() == 0;
    }

    @NotNull
    private static Thread createAndSetNameAndScheduleItsInterruptionAndGetThread(
            int delayAfterWhichThreadMustBeInterrupted, @NotNull final Runnable action, @NotNull final String threadName) {
        Thread thread = new Thread(Objects.requireNonNull(action));
        thread.setName(threadName);
        interruptThreadAfterDelayIfNotAlreadyJoined(thread, delayAfterWhichThreadMustBeInterrupted);
        thread.start();
        return thread;
    }

    @NotNull
    private static <ElementType> Optional<ElementType> getLastElementFromBuffer(
            @NotNull final Buffer<ElementType> buffer) throws NoSuchFieldException, IllegalAccessException {
        List<ElementType> actualBuffer = getActualBuffer(buffer);
        assert actualBuffer != null;
        return actualBuffer.size() > 0
                ? Optional.of(actualBuffer.get(actualBuffer.size() - 1))
                : Optional.empty();
    }

    private static void interruptThreadAfterDelayIfNotAlreadyJoined(
            @NotNull final Thread threadToBeEventuallyInterrupted, int delayInMillisecs) {
        Executors.newScheduledThreadPool(1)
                .schedule(threadToBeEventuallyInterrupted::interrupt, delayInMillisecs, TimeUnit.MILLISECONDS);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void getNumberOfElementsTest(int size) throws NoSuchFieldException, IllegalAccessException {
        for (int numberOfInsertedElements = 0; numberOfInsertedElements < size; numberOfInsertedElements++) {
            Buffer<Integer> bufferInstance = new Buffer<>(size);
            List<Integer> actualBuffer = getActualBuffer(bufferInstance);
            assert actualBuffer != null;
            actualBuffer.addAll(IntStream.range(0, numberOfInsertedElements).boxed().toList());
            assertEquals(numberOfInsertedElements, bufferInstance.getNumberOfElements());
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void insertShouldWaitIfBufferIsFull() throws NoSuchFieldException, IllegalAccessException {
        Buffer<Integer> buffer = new Buffer<>(ARBITRARY_CHOSEN_SIZE);
        fillBufferWithIntegers(buffer);
        assert buffer.getNumberOfElements() == ARBITRARY_CHOSEN_SIZE;
        final Integer oneMoreElementInsertedWhenBufferIsFull = 1;
        Thread threadThatTryToInsertOneMoreElementInBuffer =
                createAndSetNameAndScheduleItsInterruptionAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        () -> buffer.insert(oneMoreElementInsertedWhenBufferIsFull),
                        "threadThatTryToInsertOneMoreElementInBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
        } catch (InterruptedException e) {
            fail(e);
        }
        assertEquals(Thread.State.WAITING, threadThatTryToInsertOneMoreElementInBuffer.getState());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void insertShouldWaitIfBufferIsFullButRestartWhenThereIsSpace() throws NoSuchFieldException, IllegalAccessException {
        Buffer<Integer> buffer = new Buffer<>(ARBITRARY_CHOSEN_SIZE);
        fillBufferWithIntegers(buffer);
        assert buffer.getNumberOfElements() == ARBITRARY_CHOSEN_SIZE;
        final Integer oneMoreElementInsertedWhenBufferIsFull = 42;
        Thread threadThatTryToInsertOneMoreElementInBuffer =
                createAndSetNameAndScheduleItsInterruptionAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        () -> buffer.insert(oneMoreElementInsertedWhenBufferIsFull),
                        "threadThatTryToInsertOneMoreElementInBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
        } catch (InterruptedException e) {
            fail(e);
        }
        assert Thread.State.WAITING.equals(threadThatTryToInsertOneMoreElementInBuffer.getState()); // TODO : code duplication with previous test

        Thread threadThatRemoveOneElementFromBuffer =
                createAndSetNameAndScheduleItsInterruptionAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        buffer::getAndRemoveLastElement, "threadThatRemoveOneElementFromBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
            threadThatRemoveOneElementFromBuffer.join();
            threadThatTryToInsertOneMoreElementInBuffer.join();
        } catch (InterruptedException e) {
            fail(e);
        }
        Optional<?> lastElementInBufferAfterInsertingTheOneMoreElment = getLastElementFromBuffer(buffer);
        lastElementInBufferAfterInsertingTheOneMoreElment
                .ifPresentOrElse(o -> assertEquals(oneMoreElementInsertedWhenBufferIsFull, o), Assertions::fail);
    }

}