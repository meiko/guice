package com.vaadin.guice.server;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

class ErrorViewProvider implements ViewProvider{

    private final GuiceVaadinServlet guiceVaadinServlet;
    private final Class<? extends View> viewClass;

    ErrorViewProvider(GuiceVaadinServlet guiceVaadinServlet, Class<? extends View> viewClass) {
        this.guiceVaadinServlet = guiceVaadinServlet;
        this.viewClass = viewClass;
    }

    @Override
    public String getViewName(String viewAndParameters) {
        return viewAndParameters;
    }

    @Override
    public View getView(String viewName) {
        return guiceVaadinServlet.getInjector().getInstance(viewClass);
    }
}
