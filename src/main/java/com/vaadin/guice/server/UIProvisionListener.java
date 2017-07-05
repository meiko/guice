package com.vaadin.guice.server;

import com.google.inject.Binding;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static com.vaadin.guice.server.ReflectionUtils.findErrorView;
import static java.lang.String.format;

final class UIProvisionListener extends AbstractMatcher<Binding<?>> implements ProvisionListener {

    private final Optional<ErrorViewProvider> optionalErrorViewProvider;
    private final GuiceVaadin guiceVaadin;

    UIProvisionListener(GuiceVaadin guiceVaadin) {
        final Optional<Class<? extends View>> optionalErrorViewClass = findErrorView(guiceVaadin.getViews());

        optionalErrorViewProvider = optionalErrorViewClass.map(errorViewClass -> new ErrorViewProvider(guiceVaadin, errorViewClass));

        this.guiceVaadin = guiceVaadin;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return UI.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceUI.class);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {

        final UI ui = createUI(provision);

        addNavigatorOptionally(ui);
    }

    private void addNavigatorOptionally(UI ui) {
        final Class<? extends UI> uiClass = ui.getClass();

        GuiceUI annotation = uiClass.getAnnotation(GuiceUI.class);

        checkState(annotation != null);

        Class<? extends Component> viewContainerClass = annotation.viewContainer();

        if (viewContainerClass.equals(Component.class)) {
            return;
        }

        checkState(
                viewContainerClass.isAnnotationPresent(UIScope.class),
                "%s is annotated with having %s as it's viewContainer, but this class does not have a @UIScope annotation. " +
                        "ViewContainers must be put in UIScope",
                uiClass, viewContainerClass
        );

        Component defaultView = guiceVaadin.assemble(viewContainerClass);

        GuiceNavigator navigator = guiceVaadin.assemble(annotation.navigator());

        if (defaultView instanceof ViewDisplay) {
            navigator.init(ui, (ViewDisplay) defaultView);
        } else if (defaultView instanceof ComponentContainer) {
            navigator.init(ui, (ComponentContainer) defaultView);
        } else if (defaultView instanceof SingleComponentContainer) {
            navigator.init(ui, (SingleComponentContainer) defaultView);
        } else {
            throw new IllegalArgumentException(
                    format(
                            "%s is set as viewContainer() in @GuiceUI of %s, must be either ComponentContainer, SingleComponentContainer or ViewDisplay",
                            viewContainerClass,
                            uiClass
                    )
            );
        }

        optionalErrorViewProvider.ifPresent(navigator::setErrorProvider);

        guiceVaadin
                .getViewChangeListeners(uiClass)
                .stream()
                .map(guiceVaadin::assemble)
                .forEach(navigator::addViewChangeListener);

        navigator.addProvider(guiceVaadin.getViewProvider());

        ui.setNavigator(navigator);
    }

    private <T> UI createUI(ProvisionInvocation<T> provision) {
        final UI ui;

        final UIScoper uiScoper = guiceVaadin.getUiScoper();

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (uiScoper) {
            try {
                uiScoper.startInitialization();

                ui = (UI)provision.provision();

                uiScoper.endInitialization(ui);

            } catch (RuntimeException e) {
                uiScoper.rollbackInitialization();
                throw e;
            }
        }

        return ui;
    }
}
