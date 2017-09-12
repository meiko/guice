package com.vaadin.guice.server;

import com.google.inject.Injector;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

abstract class ViewProviderBase implements ViewProvider {
    protected final GuiceVaadinServlet guiceVaadinServlet;

    ViewProviderBase(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public abstract String getViewName(String viewNameAndParameters);

    @Override
    public abstract View getView(String viewName);

    View getView(Class<? extends View> viewClass) {
        final ViewScope viewScope = guiceVaadinServlet.getViewScope();
        final Injector injector = guiceVaadinServlet.getInjector();

        synchronized (viewScope) {
            try {
                viewScope.startScopeInit(viewClass);

                View view = injector.getInstance(viewClass);

                viewScope.flushInitialScopeSet(view);

                return view;
            } finally {
                viewScope.endScopeInit();
            }
        }
    }
}
