package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ObservableProperty<T> implements Observable {  // TODO : to be tested
    private T propertyValue;
    private final Observable objectContainingTheProperty;

    public ObservableProperty(@Nullable final T propertyValue, @NotNull final Observable objectContainingTheProperty) {
        this.propertyValue = propertyValue;
        this.objectContainingTheProperty = Objects.requireNonNull(objectContainingTheProperty);
    }

    public ObservableProperty(@NotNull final Observable objectContainingTheProperty) {
        this(null, Objects.requireNonNull(objectContainingTheProperty));
    }

    public String getPropertyNameOrElseThrow() {
        return getPropertyNameIfFoundOrElseThrow(objectContainingTheProperty, this);
    }

    public T getPropertyValue() {
        return propertyValue;
    }

    @NotNull
    public ObservableProperty<T> setPropertyValue(@Nullable final T propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    @NotNull
    public ObservableProperty<T> setPropertyValueAndFireIfPropertyChange(@Nullable final T propertyNewValue) {
        T oldValue = getPropertyValue();
        setPropertyValue(propertyNewValue);
        if (!Objects.equals(oldValue, propertyNewValue)) {
            firePropertyChange(getPropertyNameOrElseThrow(), oldValue, getPropertyValue());
        }
        return this;
    }

    public static String getPropertyNameIfFoundOrElseThrow( // TODO : to be tested
                                                            @NotNull final Observable whoContainsTheProperty,
                                                            @Nullable final Object theProperty) {
        return getAllFieldsAlsoInherited(Objects.requireNonNull(whoContainsTheProperty).getClass())
                .stream().unordered().parallel()
                .filter(aField -> {
                    try {
                        aField.setAccessible(true);
                        return aField.get(whoContainsTheProperty) == theProperty; // check object reference
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .map(Field::getName)
                .findAny()
                .orElseThrow();
    }

    public static List<Field> getAllFieldsAlsoInherited(@NotNull final Class<?> clazzWhereToSearchFields) {
        // TODO : to be tested
        return getAllFieldsAlsoInheritedRecursive(new ArrayList<>(), Objects.requireNonNull(clazzWhereToSearchFields));
    }

    public static List<Field> getAllFieldsAlsoInheritedRecursive(@NotNull final List<Field> listOfFields, @NotNull final Class<?> clazzWhereToSearchFields) {
        Objects.requireNonNull(listOfFields).addAll(
                Arrays.asList(Objects.requireNonNull(clazzWhereToSearchFields).getDeclaredFields()));
        if (clazzWhereToSearchFields.getSuperclass() != null) {
            getAllFieldsAlsoInheritedRecursive(listOfFields, clazzWhereToSearchFields.getSuperclass());
        }
        return listOfFields;
    }
}