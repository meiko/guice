package com.vaadin.guice.server;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

import com.vaadin.navigator.View;

import java.util.Iterator;
import java.util.Set;

class NavigableViewsProvider implements Provider<Set<Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    NavigableViewsProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Set<Class<? extends View>> get() {
        final Iterator<Class<? extends View>> iterator = guiceVaadinServlet
                .getViews()
                .stream()
                .filter(guiceVaadinServlet::isNavigableForCurrentUI)
                .iterator();

        return ImmutableSet.copyOf(iterator);
    }
}
