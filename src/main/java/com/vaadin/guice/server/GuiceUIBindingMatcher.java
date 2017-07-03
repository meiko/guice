package com.vaadin.guice.server;

import com.google.inject.Binding;
import com.google.inject.matcher.AbstractMatcher;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.ui.UI;

class GuiceUIBindingMatcher extends AbstractMatcher<Binding<?>> {
    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return UI.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceUI.class);
    }
}
