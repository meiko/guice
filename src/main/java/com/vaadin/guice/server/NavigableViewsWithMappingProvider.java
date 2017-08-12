package com.vaadin.guice.server;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;

import java.util.Map;

class NavigableViewsWithMappingProvider implements Provider<Map<Class<? extends View>, String>> {

    private final GuiceVaadinServlet guiceVaadinServlet;

    NavigableViewsWithMappingProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Map<Class<? extends View>, String> get() {

        ImmutableMap.Builder<Class<? extends View>, String> builder = ImmutableMap.builder();

        guiceVaadinServlet
                .getViews()
                .stream()
                .filter(guiceVaadinServlet::isNavigableForCurrentUI)
                .forEach(c -> builder.put(c, c.getAnnotation(GuiceView.class).value()));

        return builder.build();
    }
}
