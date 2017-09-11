package com.vaadin.guice.server;

import com.google.inject.Injector;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toMap;

/**
 * A Vaadin {@link ViewProvider} that fetches the views from the guice application context. The
 * views must implement the {@link View} interface and be annotated with the {@link GuiceView}
 * annotation. <p>
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 * @author Bernd Hopp (bernd@vaadin.com)
 * @see GuiceView
 */
class GuiceViewProvider implements ViewProvider {

    private final Map<String, Class<? extends View>> viewNamesToViewClassesMap;
    private final GuiceVaadinServlet guiceVaadinServlet;

    GuiceViewProvider(Set<Class<? extends View>> viewClasses, GuiceVaadinServlet guiceVaadinServlet) {

        viewClasses.forEach(c -> checkArgument(c.isAnnotationPresent(GuiceView.class), "GuiceView-annotation missing at %s", c));

        viewNamesToViewClassesMap = viewClasses
                .stream()
                .collect(
                        toMap(
                                viewClass -> viewClass.getAnnotation(GuiceView.class).value().toLowerCase(),
                                viewClass -> viewClass
                        )
                );

        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    public String getViewName(String viewNameAndParameters) {
        checkNotNull(viewNameAndParameters);

        final int indexOfDelimiter = viewNameAndParameters.indexOf('/');

        String viewName = indexOfDelimiter != -1
                ? viewNameAndParameters.substring(0, indexOfDelimiter)
                : viewNameAndParameters;

        //view-names are case-insensitive
        viewName = viewName.toLowerCase();

        final Class<? extends View> viewClass = viewNamesToViewClassesMap.get(viewName);

        //if no view is registered under this name, null is to be returned
        if (viewClass == null) {
            return null;
        }

        final UI currentUI = UI.getCurrent();

        return guiceVaadinServlet.appliesForUI(currentUI.getClass(), viewClass) ? viewName : null;
    }

    @Override
    public View getView(String viewName) {
        Class<? extends View> viewClass = viewNamesToViewClassesMap.get(viewName);

        final ViewScope viewScope = guiceVaadinServlet.getViewScope();
        final Injector injector = guiceVaadinServlet.getInjector();

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
