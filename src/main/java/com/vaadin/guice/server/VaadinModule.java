package com.vaadin.guice.server;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.guice.annotation.NavigableViewClasses;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.guice.annotation.VaadinSessionScope;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

class VaadinModule extends AbstractModule {

    private final GuiceVaadinServlet guiceVaadinServlet;

    VaadinModule(GuiceVaadinServlet GuiceVaadinServlet) {
        this.guiceVaadinServlet = GuiceVaadinServlet;
    }

    @Override
    protected void configure() {
        bindScope(UIScope.class, guiceVaadinServlet.getUiScoper());
        bindScope(GuiceView.class, guiceVaadinServlet.getUiScoper());
        bindScope(GuiceUI.class, guiceVaadinServlet.getVaadinSessionScoper());
        bindScope(VaadinSessionScope.class, guiceVaadinServlet.getVaadinSessionScoper());

        guiceVaadinServlet.getUis().forEach(this::bindUI);

        final TypeLiteral<Set<Class<? extends View>>> setOfViewClassesType = new TypeLiteral<Set<Class<? extends View>>>() {
        };

        bind(setOfViewClassesType).annotatedWith(NavigableViewClasses.class).toProvider(new NavigableViewsProvider(guiceVaadinServlet));
    }

    private <T extends UI> void bindUI(Class<T> uiClass) {
        try {
            checkArgument(uiClass.getConstructors().length == 1);

            uiClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("no default constructor found in " + uiClass, e);
        }

        bind(uiClass).toProvider(new UIProvider<>(guiceVaadinServlet, uiClass));
    }
}
