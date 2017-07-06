package com.vaadin.guice.server;

import com.google.common.collect.ImmutableMap;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.vaadin.guice.server.PathUtil.removeParametersFromViewName;

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

    private static final long serialVersionUID = 6113953554214462809L;

    private final Map<String, Class<? extends View>> viewNamesToViewClassesMap;
    private final GuiceVaadin guiceVaadin;

    GuiceViewProvider(Set<Class<? extends View>> viewClasses, GuiceVaadin guiceVaadin) {
        viewNamesToViewClassesMap = scanForViews(viewClasses);
        this.guiceVaadin = guiceVaadin;
    }

    private Map<String, Class<? extends View>> scanForViews(Set<Class<? extends View>> viewClasses) {
        ImmutableMap.Builder<String, Class<? extends View>> viewMapBuilder = ImmutableMap.builder();

        for (Class<? extends View> viewClass : viewClasses) {

            GuiceView annotation = viewClass.getAnnotation(GuiceView.class);

            checkState(annotation != null);

            viewMapBuilder.put(annotation.value(), viewClass);
        }

        return viewMapBuilder.build();
    }

    @Override
    public String getViewName(String viewAndParameters) {

        final String viewName = removeParametersFromViewName(viewAndParameters);

        return viewNamesToViewClassesMap.containsKey(viewName) ? viewName : null;
    }

    @Override
    public View getView(String viewName) {

        Class<? extends View> viewClass = viewNamesToViewClassesMap.get(viewName);

        checkArgument(viewClass != null, "no view for name \"%s\" registered", viewName);

        return guiceVaadin.getInjector().getInstance(viewClass);
    }
}
