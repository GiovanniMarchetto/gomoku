package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    private final static int MAX_BUFFER_SIZE_USED_IN_TEST = 10000;
    @NotNull
    private final static String actualBufferFieldNameInClass = "buffer";
    @NotNull
    private final static String sizeFieldNameInClass = "size";

    private final static int ARBITRARY_CHOSEN_SIZE = 10;
    private final static int REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START = 10;
    private final static int REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED =
            2 * REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START;

    @NotNull
    private Buffer<Integer> bufferOfIntegerUsedInTests;

    @SuppressWarnings("unchecked")  // cast inner buffer to list of correct type
    @Nullable
    private static <ElementType> List<ElementType> getActualBuffer(@NotNull final Buffer<ElementType> buffer)
            throws NoSuchFieldException, IllegalAccessException {
        return (List<ElementType>) TestUtility.getFieldValue(actualBufferFieldNameInClass, Objects.requireNonNull(buffer));
    }

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

    @BeforeEach
    void setUp() {
        bufferOfIntegerUsedInTests = new Buffer<>(ARBITRARY_CHOSEN_SIZE);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectSize(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue(sizeFieldNameInClass, buffer), size);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectlyInitializedBuffer(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue(actualBufferFieldNameInClass, buffer), new ArrayList<Integer>(size));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    private <ElementType> boolean isBufferEmpty(@NotNull final Buffer<ElementType> buffer) {
        return Objects.requireNonNull(buffer).getNumberOfElements() == 0;
    }

    @NotNull
    private static Thread createAndSetNameAndScheduleItsInterruptionAndStartAndGetThread(
            int delayAfterWhichThreadMustBeInterrupted, @NotNull final Runnable action, @NotNull final String threadName) {
        Thread thread = new Thread(Objects.requireNonNull(action));
        thread.setName(threadName);
        TestUtility.interruptThreadAfterDelayIfNotAlreadyJoined(thread, delayAfterWhichThreadMustBeInterrupted);
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

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void getNumberOfElementsTest(int bufferSize) {
        IntStream.range(0, bufferSize)
                .forEach(numberOfInsertedElements -> {
                    Buffer<Integer> bufferInstance = new Buffer<>(bufferSize);
                    List<Integer> actualBuffer = null;
                    try {
                        actualBuffer = getActualBuffer(bufferInstance);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        fail(e);
                    }
                    assert actualBuffer != null;
                    actualBuffer.addAll(IntStream.range(0, numberOfInsertedElements).boxed().toList());
                    assertEquals(numberOfInsertedElements, bufferInstance.getNumberOfElements());
                });
    }


    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void insertOneElementWhenBufferIsEmpty(int element) {
        assert isBufferEmpty(bufferOfIntegerUsedInTests);
        bufferOfIntegerUsedInTests.insert(element);
        final int NUMBER_OF_INSERTED_ELEMENTS = 1;
        assertEquals(NUMBER_OF_INSERTED_ELEMENTS, bufferOfIntegerUsedInTests.getNumberOfElements());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void testThatInsertShouldWaitIfBufferIsFull(int oneMoreElementInsertedWhenBufferIsFull)
            throws NoSuchFieldException, IllegalAccessException {
        Thread threadThatTryToInsertOneMoreElementInBuffer =
                createAndStartAndGetThreadWhichTriesToInsertOneMoreElementInBufferWhenFull(oneMoreElementInsertedWhenBufferIsFull);
        assertEquals(Thread.State.WAITING, threadThatTryToInsertOneMoreElementInBuffer.getState());
    }

    @NotNull
    private Thread createAndStartAndGetThreadWhichTriesToInsertOneMoreElementInBufferWhenFull(
            int oneMoreElementInsertedWhenBufferIsFull) throws NoSuchFieldException, IllegalAccessException {
        fillBufferWithIntegers(bufferOfIntegerUsedInTests);
        assert bufferOfIntegerUsedInTests.getNumberOfElements() == ARBITRARY_CHOSEN_SIZE;
        Thread threadThatTryToInsertOneMoreElementInBuffer =
                createAndSetNameAndScheduleItsInterruptionAndStartAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        () -> bufferOfIntegerUsedInTests.insert(oneMoreElementInsertedWhenBufferIsFull),
                        "threadThatTryToInsertOneMoreElementInBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
        } catch (InterruptedException e) {
            fail(e);
        }
        return threadThatTryToInsertOneMoreElementInBuffer;
    }

    @Test
    void insertShouldWaitIfBufferIsFullButRestartWhenThereIsSpace() throws NoSuchFieldException, IllegalAccessException {
        int oneMoreElementInsertedWhenBufferIsFull = 1234;
        Thread threadThatTryToInsertOneMoreElementInBuffer =
                createAndStartAndGetThreadWhichTriesToInsertOneMoreElementInBufferWhenFull(oneMoreElementInsertedWhenBufferIsFull);
        Thread threadThatRemoveOneElementFromBuffer =
                createAndSetNameAndScheduleItsInterruptionAndStartAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        bufferOfIntegerUsedInTests::getAndRemoveLastElement, "threadThatRemoveOneElementFromBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
            threadThatRemoveOneElementFromBuffer.join();
            threadThatTryToInsertOneMoreElementInBuffer.join();
        } catch (InterruptedException e) {
            fail(e);
        }
        Optional<?> lastElementInBufferAfterInsertingTheOneMoreElement = getLastElementFromBuffer(bufferOfIntegerUsedInTests);
        lastElementInBufferAfterInsertingTheOneMoreElement
                .ifPresentOrElse(o -> assertEquals(oneMoreElementInsertedWhenBufferIsFull, o), Assertions::fail);
    }

    @Test
    void getAndRemoveLastElementWhenBufferIsNotEmpty() throws NoSuchFieldException, IllegalAccessException {
        fillBufferWithIntegers(bufferOfIntegerUsedInTests);
        assert !isBufferEmpty(bufferOfIntegerUsedInTests);
        int expectedSizeAfterRemovingOneElement = bufferOfIntegerUsedInTests.getNumberOfElements() - 1;
        bufferOfIntegerUsedInTests.getAndRemoveLastElement();
        assertEquals(expectedSizeAfterRemovingOneElement, bufferOfIntegerUsedInTests.getNumberOfElements());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_LOWER_THAN_10000_PROVIDER_RESOURCE_LOCATION)
    void insertOneElementThenRemoveTheLastElementWhichShouldBeEqualsToTheJustInsertedOne(int element) {
        assert isBufferEmpty(bufferOfIntegerUsedInTests);
        bufferOfIntegerUsedInTests.insert(element);
        assert bufferOfIntegerUsedInTests.getNumberOfElements() == 1;
        Integer elementRemovedFromBuffer = bufferOfIntegerUsedInTests.getAndRemoveLastElement();
        assert isBufferEmpty(bufferOfIntegerUsedInTests);
        assertEquals(element, elementRemovedFromBuffer);
    }

    @Test
    void removeShouldWaitIfBufferIsEmpty() throws IOException {
        Thread threadThatTryToRemoveOneElementFromBuffer =
                createAndStartAndGetThreadWhichTriesToRemoveOneElementFromBufferWhenEmpty(
                        new ObjectOutputStream(new ByteArrayOutputStream()));
        assertEquals(Thread.State.WAITING, threadThatTryToRemoveOneElementFromBuffer.getState());
    }

    @NotNull
    private Thread createAndStartAndGetThreadWhichTriesToRemoveOneElementFromBufferWhenEmpty(
            @NotNull final ObjectOutputStream whereToWriteTheObjectRemovedFromTheBuffer) {
        assert isBufferEmpty(bufferOfIntegerUsedInTests);
        Thread threadThatTryToRemoveOneElementFromBuffer =
                createAndSetNameAndScheduleItsInterruptionAndStartAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        () -> {
                            try {
                                Objects.requireNonNull(whereToWriteTheObjectRemovedFromTheBuffer)
                                        .writeObject(bufferOfIntegerUsedInTests.getAndRemoveLastElement());
                                whereToWriteTheObjectRemovedFromTheBuffer.flush();
                            } catch (IOException e) {
                                fail(e);
                            }
                        },
                        "threadThatTryToRemoveOneElementFromBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
        } catch (InterruptedException e) {
            fail(e);
        }
        return threadThatTryToRemoveOneElementFromBuffer;
    }

    @Test
    void removeShouldWaitIfBufferIsEmptyButRestartWhenThereIsAtLeastOneElement()
            throws IOException, ClassNotFoundException {
        int oneElementToInsert = 1234;
        ByteArrayOutputStream whereThreadWhichRemoveFromBufferWillWriteTheRemovedObject =
                new ByteArrayOutputStream();
        Thread threadThatTryToRemoveOneElementFromBuffer =
                createAndStartAndGetThreadWhichTriesToRemoveOneElementFromBufferWhenEmpty(
                        new ObjectOutputStream(whereThreadWhichRemoveFromBufferWillWriteTheRemovedObject));
        Thread threadThatInsertOneElementIntoBuffer =
                createAndSetNameAndScheduleItsInterruptionAndStartAndGetThread(
                        REASONABLE_MILLISECS_AFTER_WHICH_THREAD_MUST_BE_INTERRUPTED,
                        () -> bufferOfIntegerUsedInTests.insert(oneElementToInsert),
                        "threadThatInsertOneElementIntoBuffer");
        try {
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THREAD_TO_START);
            threadThatInsertOneElementIntoBuffer.join();
            threadThatTryToRemoveOneElementFromBuffer.join();
        } catch (InterruptedException e) {
            fail(e);
        }
        Object elementRemovedFromBuffer = new ObjectInputStream(
                new ByteArrayInputStream(whereThreadWhichRemoveFromBufferWillWriteTheRemovedObject.toByteArray()))
                .readObject();
        assertEquals(oneElementToInsert, elementRemovedFromBuffer);
    }

}