package it.units.sdm.gomoku.ui.support;

@SuppressWarnings("unused") // enum values are used
public enum BooleanAnswers implements ExposedEnum { // TODO : to be tested
    YES('Y'),
    NO('N');

    private final char exposedValue;

    BooleanAnswers(char exposedValue) {
        this.exposedValue = exposedValue;
    }

    @Override
    public String getExposedValueOf() {
        return String.valueOf(exposedValue);
    }
}
