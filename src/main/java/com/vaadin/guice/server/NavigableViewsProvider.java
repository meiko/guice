package com.vaadin.guice.server;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

class NavigableViewsProvider implements Provider<Set<Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    private final Map<Class<? extends UI>, Set<Class<? extends View>>> cache = new ConcurrentHashMap<>();

    NavigableViewsProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Set<Class<? extends View>> get() {
        final UI currentUI = checkNotNull(UI.getCurrent());

        return cache.computeIfAbsent(currentUI.getClass(), this::compute);
    }

    private Set<Class<? extends View>> compute(Class<? extends UI> currentUIClass) {
        return ImmutableSet.copyOf(
            guiceVaadinServlet
                .getViews()
                .stream()
                .filter(viewClass -> guiceVaadinServlet.isNavigable(currentUIClass, viewClass))
                .iterator()
        );
    }
}
