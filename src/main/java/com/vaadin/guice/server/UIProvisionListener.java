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

    private final GuiceVaadinServlet servlet;

    UIProvisionListener(GuiceVaadinServlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return UI.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceUI.class);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {

        UI ui;

        synchronized (servlet.getUiScoper()) {
            try {
                ui = (UI) provision.provision();
                process(ui);
                servlet.getUiScoper().endInit(ui);
            } catch (RuntimeException e) {
                servlet.getUiScoper().rollback();
                throw e;
            }
        }
    }

    private void process(UI ui) {
        final Class<? extends UI> uiClass = ui.getClass();

        GuiceUI annotation = uiClass.getAnnotation(GuiceUI.class);

        checkState(annotation != null);

        Class<? extends Component> contentClass = annotation.content();

        if (!contentClass.equals(Component.class)) {
            checkState(
                    contentClass.isAnnotationPresent(UIScope.class),
                    "%s is annotated with having %s as it's viewContainer, but this class does not have a @UIScope annotation. " +
                            "ViewContainers must be put in UIScope",
                    uiClass, contentClass
            );

            Component content = servlet.getInjector().getInstance(contentClass);

            ui.setContent(content);
        }

        Class<? extends Component> viewContainerClass = annotation.viewContainer();

        if (!viewContainerClass.equals(Component.class)) {

            checkState(
                    viewContainerClass.isAnnotationPresent(UIScope.class),
                    "%s is annotated with having %s as it's viewContainer, but this class does not have a @UIScope annotation. " +
                            "ViewContainers must be put in UIScope",
                    uiClass, viewContainerClass
            );


            Component defaultView = servlet.getInjector().getInstance(viewContainerClass);

            GuiceNavigator navigator = servlet.getInjector().getInstance(annotation.navigator());

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

                final ViewProvider errorProvider = servlet.getInjector().getInstance(annotation.errorProvider());

                navigator.setErrorProvider(errorProvider);
            } else if (!annotation.errorView().equals(View.class)) {
                navigator.setErrorView(annotation.errorView());
            }

            servlet
                    .getViewChangeListeners(uiClass)
                    .stream()
                    .map(servlet.getInjector()::getInstance)
                    .forEach(navigator::addViewChangeListener);

            navigator.addProvider(servlet.getViewProvider());

            ui.setNavigator(navigator);
        }
    }
}
