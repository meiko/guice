package com.vaadin.guice.server;

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

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static com.vaadin.guice.server.PathUtil.removeParametersFromViewName;
import static com.vaadin.guice.server.ReflectionUtils.findErrorView;
import static java.lang.String.format;

final class NavigatorManager implements ProvisionListener {

    private final Optional<Class<? extends View>> errorViewClassOptional;
    private final GuiceVaadin guiceVaadin;

    NavigatorManager(GuiceVaadin guiceVaadin) {
        this.errorViewClassOptional = findErrorView(guiceVaadin.getViews());
        this.guiceVaadin = guiceVaadin;
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        UI ui = (UI)provision.provision();

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

        errorViewClassOptional.ifPresent(errorViewClass -> navigator.setErrorProvider(
                new ViewProvider() {
                    @Override
                    public String getViewName(String viewAndParameters) {
                        return removeParametersFromViewName(viewAndParameters);
                    }

                    @Override
                    public View getView(String viewName) {
                        //noinspection OptionalGetWithoutIsPresent
                        return guiceVaadin.assemble(errorViewClass);
                    }
                }
        ));

        guiceVaadin
                .getViewChangeListeners(uiClass)
                .stream()
                .map(guiceVaadin::assemble)
                .forEach(navigator::addViewChangeListener);

        navigator.addProvider(guiceVaadin.getViewProvider());

        ui.setNavigator(navigator);
    }
}
