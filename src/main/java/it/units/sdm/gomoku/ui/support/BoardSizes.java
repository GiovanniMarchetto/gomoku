package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import org.jetbrains.annotations.NotNull;

import static it.units.sdm.gomoku.model.custom_types.PositiveOddInteger.PositiveOddIntegerType;

@SuppressWarnings("unused") // enum values are used
public enum BoardSizes implements ExposedEnum { // TODO : to be tested
    VERY_SMALL(9, 1),
    SMALL(15, 2),
    NORMAL(19, 3),
    BIG(23, 4),
    VERY_BIG(29, 5);

    private final PositiveOddInteger boardSize;
    private final int ordinalValueOfThisEnum;

    BoardSizes(@PositiveOddIntegerType int boardSize, int ordinalValueOfThisEnum) {
        this.boardSize = new PositiveOddInteger(boardSize);
        this.ordinalValueOfThisEnum = ordinalValueOfThisEnum;
    }

    @NotNull
    public PositiveOddInteger getBoardSize() {
        return boardSize;
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(ordinalValueOfThisEnum);
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

    public static BoardSizes fromString(@NotNull final String valueAsString) {
        return BoardSizes.valueOf(valueAsString.replaceAll(" ", "_"));
    }
}
