package com.vaadin.guice.server;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class NavigableViewsWithMappingProvider implements Provider<Map<String, Class<? extends View>>> {

    private final GuiceVaadinServlet guiceVaadinServlet;
    private final Map<Class<? extends UI>, Map<String, Class<? extends View>>> cache = new ConcurrentHashMap<>();

    NavigableViewsWithMappingProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public Map<String, Class<? extends View>> get() {
        final UI currentUI = UI.getCurrent();

        Class<? extends UI> uiClass;

        if (currentUI != null) {
            uiClass = currentUI.getClass();
        } else {
            uiClass = checkNotNull(guiceVaadinServlet.getUiScope().currentlyCreatedUIClass());
        }

        return cache.computeIfAbsent(uiClass, this::compute);
    }

    private Map<String, Class<? extends View>> compute(Class<? extends UI> uiClass) {

        final GuiceUI annotation = uiClass.getAnnotation(GuiceUI.class);

        checkState(annotation != null);

        ImmutableMap.Builder<String, Class<? extends View>> builder = ImmutableMap.builder();

        guiceVaadinServlet
                .getViewClasses()
                .stream()
                .filter(viewClass -> guiceVaadinServlet.appliesForUI(uiClass, viewClass))
                .filter(viewClass -> !viewClass.equals(annotation.errorView()))
                .forEach(c -> builder.put(c.getAnnotation(GuiceView.class).value(), c));

        return builder.build();
    }
}
