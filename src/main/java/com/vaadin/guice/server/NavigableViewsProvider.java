package com.vaadin.guice.server;

import com.google.common.collect.Sets;
import com.google.inject.Provider;

import com.vaadin.navigator.View;

import java.util.Set;

class NavigableViewsProvider implements Provider<Set<Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    NavigableViewsProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Set<Class<? extends View>> get() {
        return Sets.filter(guiceVaadinServlet.getViews(), guiceVaadinServlet::isNavigableForCurrentUI);
    }
}
