package com.vaadin.guice.server;

import com.google.inject.Provider;

import com.vaadin.navigator.View;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

class NavigableViewsProvider implements Provider<Set<Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    NavigableViewsProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Set<Class<? extends View>> get() {
        return guiceVaadinServlet
                .getViews()
                .stream()
                .filter(guiceVaadinServlet::isNavigableForCurrentUI)
                .collect(toSet());
    }
}
