package com.vaadin.guice.server;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

import static com.vaadin.guice.server.PathUtil.removeParametersFromViewName;

class ErrorViewProvider implements ViewProvider {
    private final GuiceVaadin guiceVaadin;
    private final Class<? extends View> errorViewClass;

    public ErrorViewProvider(GuiceVaadin guiceVaadin, Class<? extends View> errorViewClass) {
        this.guiceVaadin = guiceVaadin;
        this.errorViewClass = errorViewClass;
    }

    @Override
    public String getViewName(String viewAndParameters) {
        return removeParametersFromViewName(viewAndParameters);
    }

    @Override
    public View getView(String viewName) {
        return guiceVaadin.assemble(errorViewClass);
    }
}
