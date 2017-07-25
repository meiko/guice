package com.vaadin.guice.server;

import com.google.inject.Provider;

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

class UIProvider<T extends UI> implements Provider<T> {
    private final GuiceVaadinServlet guiceVaadinServlet;
    private final Class<T> uiClass;

    public UIProvider(GuiceVaadinServlet guiceVaadinServlet, Class<T> uiClass) {
        this.guiceVaadinServlet = guiceVaadinServlet;
        this.uiClass = uiClass;
    }

    @Override
    public T get() {
        try {
            T ui = uiClass.newInstance();

            UI.setCurrent(ui);

            guiceVaadinServlet.getInjector().injectMembers(ui);

            GuiceUI annotation = uiClass.getAnnotation(GuiceUI.class);

            checkState(annotation != null);

            Class<? extends Component> contentClass = annotation.content();

            if (!contentClass.equals(Component.class)) {
                checkState(
                        contentClass.isAnnotationPresent(com.vaadin.guice.annotation.UIScope.class),
                        "%s is annotated with having %s as it's viewContainer, but this class does not have a @UIScope annotation. " +
                                "ViewContainers must be put in UIScope",
                        uiClass, contentClass
                );

                Component content = guiceVaadinServlet.getInjector().getInstance(contentClass);

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


                Component defaultView = guiceVaadinServlet.getInjector().getInstance(viewContainerClass);

                GuiceNavigator navigator = guiceVaadinServlet.getInjector().getInstance(annotation.navigator());

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

                    final ViewProvider errorProvider = guiceVaadinServlet.getInjector().getInstance(annotation.errorProvider());

                    navigator.setErrorProvider(errorProvider);
                } else if (!annotation.errorView().equals(View.class)) {
                    navigator.setErrorView(annotation.errorView());
                }

                guiceVaadinServlet
                        .getViewChangeListeners(uiClass)
                        .stream()
                        .map(guiceVaadinServlet.getInjector()::getInstance)
                        .forEach(navigator::addViewChangeListener);

                navigator.addProvider(guiceVaadinServlet.getViewProvider());

                ui.setNavigator(navigator);
            }

            return ui;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
