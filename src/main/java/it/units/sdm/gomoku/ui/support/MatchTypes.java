package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public enum MatchTypes implements ExposedEnum {
    CPU_VS_CPU(0),
    PERSON_VS_CPU(1),
    PERSON_VS_PERSON(2);
    private final NonNegativeInteger numberOfHumanPlayers;

    MatchTypes(@PositiveIntegerType int numberOfHumanPlayers) {
        this.numberOfHumanPlayers = new NonNegativeInteger(numberOfHumanPlayers);
    }

    @PositiveIntegerType
    public int getNumberOfHumanPlayers() {
        return numberOfHumanPlayers.intValue();
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(getNumberOfHumanPlayers());
    }
}
