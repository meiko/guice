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

        for (Class<? extends View> viewClass : guiceVaadinServlet.getViews()) {
            if(!guiceVaadinServlet.isNavigableForCurrentUI(viewClass)){
                continue;
            }

            builder.put(viewClass, viewClass.getAnnotation(GuiceView.class).value());
        }

        return builder.build();
    }
}
