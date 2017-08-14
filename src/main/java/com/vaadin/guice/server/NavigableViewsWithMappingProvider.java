package com.vaadin.guice.server;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

class NavigableViewsWithMappingProvider implements Provider<Map<Class<? extends View>, String>> {

    private final GuiceVaadinServlet guiceVaadinServlet;
    private final Map<Class<? extends UI>, Map<Class<? extends View>, String>> cache = new ConcurrentHashMap<>();

    NavigableViewsWithMappingProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Map<Class<? extends View>, String> get() {

        final UI currentUI = checkNotNull(UI.getCurrent());

        final Class<? extends UI> currentUIClass = currentUI.getClass();

        return cache.computeIfAbsent(currentUIClass, this::compute);
    }

    private Map<Class<? extends View>, String> compute(Class<? extends UI> uiClass){
        ImmutableMap.Builder<Class<? extends View>, String> builder = ImmutableMap.builder();

        guiceVaadinServlet
                .getViews()
                .stream()
                .filter(viewClass -> guiceVaadinServlet.isNavigable(uiClass, viewClass))
                .forEach(c -> builder.put(c, c.getAnnotation(GuiceView.class).value()));

        return builder.build();
    }
}
