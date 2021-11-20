package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import static it.units.sdm.gomoku.model.custom_types.NonNegativeInteger.NonNegativeIntegerType;
import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Buffer<ElementType> {

    @NotNull
    private final List<ElementType> buffer;
    @PositiveIntegerType
    private final int BUFFER_CAPACITY;

    public Buffer(@PositiveIntegerType int bufferCapacity) {
        if (!PositiveInteger.isValid(bufferCapacity)) {
            throw new IllegalArgumentException("Invalid size: it must be a positive integer.");
        }
        this.BUFFER_CAPACITY = bufferCapacity;
        this.buffer = new CopyOnWriteArrayList<>();
    }

    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }

    @NonNegativeIntegerType
    public synchronized int getNumberOfElements() {
        return buffer.size();
    }

    public synchronized void insert(@Nullable final ElementType element) {
        waitWhileTheBufferIsFull();
        buffer.add(element);
        notify();
    }

    @Nullable
    public synchronized ElementType getAndRemoveLastElement() {
        waitWhileTheBufferIsEmpty();
        int indexOfElementToGetAndRemove = buffer.size() - 1;
        ElementType toReturn = buffer.get(indexOfElementToGetAndRemove);
        buffer.remove(indexOfElementToGetAndRemove);
        notify();
        return toReturn;
    }

    private void waitWhileTheBufferIsFull() {
        waitWhileThereAreNElementsInBuffer(BUFFER_CAPACITY);
    }

    private void waitWhileTheBufferIsEmpty() {
        waitWhileThereAreNElementsInBuffer(0);
    }

    private void waitWhileThereAreNElementsInBuffer(int N) {
        while (getNumberOfElements() == N) {
            try {
                wait();
            } catch (InterruptedException e) {
                Utility.getLoggerOfClass(getClass()).log(Level.WARNING, "Interrupted", e);
            }
        }
    }

}