package it.units.sdm.gomoku.mvvm_library.views.gui_items;

import it.units.sdm.gomoku.mvvm_library.support.ActionOnObservedPropertyChange;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.mvvm_library.views.View;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generic button which can be clicked to fire event specified as constructor parameter.
 */
public class CommanderButton extends AbstractCommanderGUIItemWrapper<Button> {

    private final List<ActionOnObservedPropertyChange> actionOnObservedEventPropertyChanges;


    private CommanderButton(
            String textOfButton,
            View containerView,
            Viewmodel vm,
            ActionOnObservedPropertyChange... actionsOnObserveds) {

        super(containerView, new Button(), vm);

        this.actionOnObservedEventPropertyChanges =
                actionsOnObserveds == null ?
                        new ArrayList<>(0) :
                        new ArrayList<>(Arrays.asList(actionsOnObserveds));

        getGUIItem().setText(textOfButton);
    }

    public CommanderButton(
            String textOfButton,
            View containerView,
            Viewmodel vm,
            EventHandler<? super Event> onClickEventHandler,
            ActionOnObservedPropertyChange... actionsOnObserveds) {

        this(textOfButton, containerView, vm, actionsOnObserveds);

        // Add event handler to the button
        getGUIItem().addEventHandler(MouseEvent.MOUSE_RELEASED, onClickEventHandler);
    }

    /**
     * When clicked, the {@link PropertyChangeEvent} will be fired from the {@link View}
     * given as argument; the fired {@link PropertyChangeEvent} will have the same name
     * as specified in the parameter and will have <code>null</code> as <code>oldValue</code>
     * and the current showed text as <code>newValue</code>.
     */
    public <T> CommanderButton(
            String textOfButton,
            View containerView,
            Viewmodel vm,
            String propertyNameOfTheEventToFireWhenClicked,
            Supplier<T> observedObjectOnClickSupplierToGetTheNewValue,
            ActionOnObservedPropertyChange... actionsOnObserveds) {

        this(textOfButton, containerView, vm, actionsOnObserveds);

        // Notify Viewmodel when the button is clicked
        addActionOnEvent(
                MouseEvent.MOUSE_RELEASED,
                e -> makeTheViewToFirePropertyChange(propertyNameOfTheEventToFireWhenClicked, null, observedObjectOnClickSupplierToGetTheNewValue.get())
        );
    }

    public <T extends Event> void addActionOnEvent(
            EventType<T> eventType,
            EventHandler<T> eventHandler) {
        getGUIItem().addEventHandler(eventType, eventHandler);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evtFromTheObservedViewModel) {
        this.actionOnObservedEventPropertyChanges
                .stream().unordered().parallel()
                .filter(actionOnObservedPropertyChange -> actionOnObservedPropertyChange.isSamePropertyName(evtFromTheObservedViewModel))
                .forEach(actionOnObservedPropertyChange -> actionOnObservedPropertyChange.performAction(evtFromTheObservedViewModel));
    }

}