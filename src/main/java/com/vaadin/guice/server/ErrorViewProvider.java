package com.vaadin.guice.server;

import com.vaadin.navigator.View;

class ErrorViewProvider extends ViewProviderBase {

    private final Class<? extends View> viewClass;
    private View instance;

    ErrorViewProvider(GuiceVaadinServlet guiceVaadinServlet, Class<? extends View> viewClass) {
        super(guiceVaadinServlet);
        this.viewClass = viewClass;
    }

    @Override
    public String getViewName(String viewAndParameters) {
        return viewAndParameters;
    }

    @Override
    public View getView(String viewName) {
        if(instance == null){
            instance = getView(viewClass);
        }

        return instance;
    }
}
