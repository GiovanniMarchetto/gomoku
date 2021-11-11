package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public enum BoardSizes implements ExposedEnum {
    VERY_SMALL(9, 1),
    SMALL(15, 2),
    NORMAL(19, 3),
    BIG(23, 4),
    VERY_BIG(29, 5);

    private final PositiveInteger boardSize;
    private final int ordinalValueOfThisEnum;

    BoardSizes(@PositiveIntegerType int boardSize, int ordinalValueOfThisEnum) {
        this.boardSize = new PositiveInteger(boardSize);
        this.ordinalValueOfThisEnum = ordinalValueOfThisEnum;
    }

    public static BoardSizes fromString(@NotNull final String valueAsString) {
        return BoardSizes.valueOf(valueAsString.replaceAll(" ", "_"));
    }

    public static Optional<BoardSizes> fromBoardSizeValue(@NotNull final PositiveInteger boardSizeValue) {
        return Arrays.stream(BoardSizes.values())
                .filter(boardSizeEnum -> boardSizeEnum.getBoardSize().equals(Objects.requireNonNull(boardSizeValue)))
                .findAny();
    }

    @NotNull
    public PositiveInteger getBoardSize() {
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
}
