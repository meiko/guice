package com.vaadin.guice.server;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

final class UIProvisionListener extends AbstractMatcher<Binding<?>> implements ProvisionListener {

    private final GuiceVaadin guiceVaadin;

    UIProvisionListener(GuiceVaadin guiceVaadin) {
        this.guiceVaadin = guiceVaadin;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return UI.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceUI.class);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {

        UI ui = (UI) provision.provision();

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

        final Injector injector = guiceVaadin.getInjector();

        Component defaultView = injector.getInstance(viewContainerClass);

        GuiceNavigator navigator = injector.getInstance(annotation.navigator());

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

        if (!annotation.errorProvider().equals(ViewProvider.class)) {
            checkArgument(annotation.errorView().equals(View.class), "GuiceUI#errorView and GuiceUI#errorProvider cannot be set both");

            final ViewProvider errorProvider = injector.getInstance(annotation.errorProvider());

            navigator.setErrorProvider(errorProvider);
        } else if (!annotation.errorView().equals(View.class)) {
            navigator.setErrorView(annotation.errorView());
        }

        guiceVaadin
                .getViewChangeListeners(uiClass)
                .stream()
                .map(injector::getInstance)
                .forEach(navigator::addViewChangeListener);

        navigator.addProvider(guiceVaadin.getViewProvider());

        ui.setNavigator(navigator);
    }
}
