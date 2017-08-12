package com.vaadin.guice.server;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

class NavigableViewsProvider implements Provider<Set<Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    private final Map<Class<? extends UI>, Set<Class<? extends View>>> cache = new HashMap<>();

    NavigableViewsProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Set<Class<? extends View>> get() {
        final UI currentUI = checkNotNull(UI.getCurrent());

        final Class<? extends UI> currentUIClass = currentUI.getClass();

        Set<Class<? extends View>> value = cache.get(currentUIClass);

        if (value == null) {
            final Iterator<Class<? extends View>> iterator = guiceVaadinServlet
                    .getViews()
                    .stream()
                    .filter(viewClass -> guiceVaadinServlet.isNavigable(currentUIClass, viewClass))
                    .iterator();

            value = ImmutableSet.copyOf(iterator);

            cache.put(currentUIClass, value);
        }

        return value;
    }
}
