package com.vaadin.guice.server;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;

import com.vaadin.guice.annotation.Controller;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.guice.annotation.NavigableViewClasses;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.guice.annotation.VaadinSessionScope;
import com.vaadin.guice.annotation.ViewScope;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

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
        bindScope(UIScope.class, guiceVaadinServlet.getUiScope());
        bindScope(GuiceView.class, guiceVaadinServlet.getUiScope());
        bindScope(ViewScope.class, guiceVaadinServlet.getViewScope());
        bindScope(VaadinSessionScope.class, guiceVaadinServlet.getVaadinSessionScoper());

        UISetup uiSetup = new UISetup(guiceVaadinServlet);

        bindListener(uiSetup, uiSetup);

        bind(setOfViewClassesType)
                .annotatedWith(NavigableViewClasses.class)
                .toProvider(new NavigableViewsProvider(guiceVaadinServlet));

        bind(mapOfViewClassesToStringsType)
                .annotatedWith(NavigableViewClasses.class)
                .toProvider(new NavigableViewsWithMappingProvider(guiceVaadinServlet));


        for (Class<?> controllerClass : guiceVaadinServlet.getControllerClasses()) {
            final Controller annotation = controllerClass.getAnnotation(Controller.class);

            if (UI.class.isAssignableFrom(annotation.value())) {
                //done in UISetup
                continue;
            }

            bindListener(
                    new AbstractMatcher<Binding<?>>() {
                        @Override
                        public boolean matches(Binding<?> binding) {

                            final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

                            return annotation.value().equals(rawType);
                        }
                    },
                    new ProvisionListener() {
                        @Override
                        public <T> void onProvision(ProvisionInvocation<T> provisionInvocation) {
                            provisionInvocation.provision();

                            guiceVaadinServlet.getInjector().getInstance(controllerClass);
                        }
                    }
            );
        }
    }
}
