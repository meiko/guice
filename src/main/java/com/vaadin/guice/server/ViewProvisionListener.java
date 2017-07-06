package com.vaadin.guice.server;

import com.google.inject.Binding;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;

final class ViewProvisionListener extends AbstractMatcher<Binding<?>> implements ProvisionListener {

    private final GuiceVaadin guiceVaadin;

    ViewProvisionListener(GuiceVaadin guiceVaadin) {
        this.guiceVaadin = guiceVaadin;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return View.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceView.class);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        synchronized (guiceVaadin) {
            final ViewScoper viewScoper = guiceVaadin.getViewScoper();

            try {
                viewScoper.startInitialization();

                viewScoper.endInitialization((View) provision.provision());

            } catch (RuntimeException e) {
                viewScoper.rollbackInitialization();
                throw e;
            }
        }
    }
}
