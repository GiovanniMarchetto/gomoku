package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    private final static int MAX_BUFFER_SIZE_USED_IN_TEST = 10000;
    private final static String actualBufferFieldNameInClass = "buffer";
    private final static String sizeFieldNameInClass = "size";

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

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void insertShouldWaitIfBufferIsFull() throws NoSuchFieldException, IllegalAccessException {
        final int ARBITRARY_CHOSEN_SIZE = 10;
        Buffer<Integer> buffer = new Buffer<>(ARBITRARY_CHOSEN_SIZE);
        fillBufferWithIntegers(buffer);
        assert buffer.getNumberOfElements() == ARBITRARY_CHOSEN_SIZE;
        final Integer whateverIntegerElement = 1;
        Thread threadThatTryToInsertOneMoreElementInBuffer = new Thread(() -> buffer.insert(whateverIntegerElement));
        threadThatTryToInsertOneMoreElementInBuffer.setName("threadThatTryToInsertOneMoreElementInBuffer");
        threadThatTryToInsertOneMoreElementInBuffer.start();
        try {
            final int REASONABLE_MILLISECS_TO_PERMIT_THE_THREAD_TO_START_AND_TRY_TO_INVOKE_INSERT_METHODS_OF_BUFFER = 10;
            Thread.sleep(REASONABLE_MILLISECS_TO_PERMIT_THE_THREAD_TO_START_AND_TRY_TO_INVOKE_INSERT_METHODS_OF_BUFFER);
        } catch (InterruptedException e) {
            fail(e);
        }
        assertEquals(Thread.State.WAITING, threadThatTryToInsertOneMoreElementInBuffer.getState());
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

}