package it.units.sdm.gomoku.model.custom_types;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Buffer<ContentType> {

    @NotNull
    private final List<ContentType> buffer;
    @PositiveIntegerType
    private final int size;

    public Buffer(@PositiveIntegerType int size) {
        if (!PositiveInteger.isValid(size)) {
            throw new IllegalArgumentException("Invalid size: it must be a positive integer.");
        }
        this.size = size;
        this.buffer = new ArrayList<>(size);
    }

}
