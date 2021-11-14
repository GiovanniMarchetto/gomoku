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
    private final int size;

    public Buffer(@PositiveIntegerType int size) {
        if (!PositiveInteger.isValid(size)) {
            throw new IllegalArgumentException("Invalid size: it must be a positive integer.");
        }
        this.size = size;
        this.buffer = new CopyOnWriteArrayList<>();
    }

    @NonNegativeIntegerType
    public synchronized int getNumberOfElements() {
        return buffer.size();
    }

    public synchronized void insert(@Nullable final ElementType element) {
        while (getNumberOfElements() == size) {
            try {
                wait();
            } catch (InterruptedException e) {
                Utility.getLoggerOfClass(getClass()).log(Level.WARNING, "Interrupted", e);
            }
        }
        buffer.add(element);
        notify();
    }

}
