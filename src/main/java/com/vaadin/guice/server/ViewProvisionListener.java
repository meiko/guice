package com.vaadin.guice.server;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;
import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

final class ViewProvisionListener extends AbstractMatcher<Binding<?>> implements ProvisionListener {

    private final GuiceVaadinServlet servlet;

    ViewProvisionListener(GuiceVaadinServlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        final Class<?> rawType = binding.getKey().getTypeLiteral().getRawType();

        return View.class.isAssignableFrom(rawType) && rawType.isAnnotationPresent(GuiceView.class);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        
        View view;
        
        synchronized (servlet.getViewScoper()){
            try{
                view = (View) provision.provision();
                servlet.getViewScoper().endInit(view);
            } catch (RuntimeException e){
                servlet.getViewScoper().rollback();
                throw e;
            }
        }
    }
}
