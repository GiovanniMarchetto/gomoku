package it.units.sdm.gomoku.ui.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public interface ExposedEnum {

    static <S extends ExposedEnum> boolean
    isValidExposedValueOf(@NotNull final Class<S> enumClazz, String exposedValueToCheck) {
        return getValuesOfEnum(Objects.requireNonNull(enumClazz))
                .stream()
                .anyMatch(doesExposedValueCorrespondToThisEnumValue(exposedValueToCheck));
    }

    @SuppressWarnings("unchecked")  // correct type checked before casting
    private static <T extends ExposedEnum> List<T>
    getValuesOfEnum(@NotNull final Class<T> enumClazz) {
        try {
            Object valuesAsObj;
            Object[] valuesAsObjArray;

            if (enumClazz.isEnum() &&
                    (valuesAsObj = enumClazz.getMethod("values").invoke(null))
                            .getClass().isArray() &&
                    Arrays.stream(valuesAsObjArray = (Object[]) valuesAsObj)
                            .filter(x -> x.getClass().isAssignableFrom(enumClazz))
                            .count() == valuesAsObjArray.length) {
                return Arrays.asList((T[]) valuesAsObjArray);   // correct type just checked before casting
            } else {
                throw new ClassCastException();
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
            Logger.getLogger(ExposedEnum.class.getCanonicalName()).severe("Exception: " + e);
            return new ArrayList<>(0);
        }
    }

    @NotNull
    static <T extends ExposedEnum> String
    getEnumDescriptionOf(@NotNull final Class<T> enumClazz) {
        return getValuesOfEnum(enumClazz)
                .stream()
                .map(enumVal -> enumVal.getExposedValueOf() + " for " + enumVal)
                .collect(Collectors.joining(", "));
    }

    @NotNull
    private static <S extends ExposedEnum> Predicate<S>
    doesExposedValueCorrespondToThisEnumValue(String exposedValueToCheck) {
        return enumVal -> enumVal.getExposedValueOf().equals(exposedValueToCheck);
    }

    @Nullable
    static <S extends ExposedEnum> S
    getEnumValueFromExposedValueOrNull(@NotNull final Class<S> enumClazz, String exposedValue) {
        if (isValidExposedValueOf(Objects.requireNonNull(enumClazz), exposedValue)) {
            return getValuesOfEnum(enumClazz)
                    .stream().unordered().parallel()
                    .filter(doesExposedValueCorrespondToThisEnumValue(exposedValue))
                    .findAny()
                    .orElse(null);
        } else {
            return null;
        }
    }

    String getExposedValueOf();
}
