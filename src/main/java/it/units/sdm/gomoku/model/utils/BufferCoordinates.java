package it.units.sdm.gomoku.model.utils;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;

public class BufferCoordinates {
    private Coordinates bufferCoordinate;

    private boolean isPresent() {
        return bufferCoordinate != null;
    }

    public synchronized void clear() {
        bufferCoordinate = null;
        notify();
    }

    private void wait_() {
        try {
            wait();
        } catch (InterruptedException e) {
            System.err.println("Interrupted");  // TODO : better to use try-catch or throws in method signature?
        }
    }

    public synchronized void insert(@NotNull Coordinates coordinates) {
        while (isPresent()) {                          // TODO : waste time?
            wait_();
        }
        this.bufferCoordinate = coordinates;
        notify();
    }

    public synchronized Coordinates getAndRemove() {
        while (!isPresent()) {                          // TODO : waste time?
            wait_();
        }
        Coordinates coord = bufferCoordinate;
        clear();
        notify();
        return coord;
    }
}