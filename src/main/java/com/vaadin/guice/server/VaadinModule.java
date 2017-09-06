package com.vaadin.guice.server;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import com.vaadin.guice.annotation.Controller;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.guice.annotation.NavigableViewClasses;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.guice.annotation.VaadinSessionScope;
import com.vaadin.navigator.View;

import java.util.Map;
import java.util.Set;

class VaadinModule extends AbstractModule {

    private final GuiceVaadinServlet guiceVaadinServlet;
    private final TypeLiteral<Set<Class<? extends View>>> setOfViewClassesType = new TypeLiteral<Set<Class<? extends View>>>() {
    };
    private final TypeLiteral<Map<String, Class<? extends View>>> mapOfViewClassesToStringsType = new TypeLiteral<Map<String, Class<? extends View>>>() {
    };

    VaadinModule(GuiceVaadinServlet GuiceVaadinServlet) {
        this.guiceVaadinServlet = GuiceVaadinServlet;
    }

    @Override
    protected void configure() {
        bindScope(UIScope.class, guiceVaadinServlet.getUiScoper());
        bindScope(GuiceView.class, guiceVaadinServlet.getUiScoper());
        bindScope(Controller.class, guiceVaadinServlet.getUiScoper());
        bindScope(VaadinSessionScope.class, guiceVaadinServlet.getVaadinSessionScoper());

        UISetup uiSetup = new UISetup(guiceVaadinServlet);

        bindListener(uiSetup, uiSetup);

        bind(setOfViewClassesType)
                .annotatedWith(NavigableViewClasses.class)
                .toProvider(new NavigableViewsProvider(guiceVaadinServlet));

        bind(mapOfViewClassesToStringsType)
                .annotatedWith(NavigableViewClasses.class)
                .toProvider(new NavigableViewsWithMappingProvider(guiceVaadinServlet));
    }
}
