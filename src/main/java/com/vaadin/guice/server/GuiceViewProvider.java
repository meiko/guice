package com.vaadin.guice.server;

import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

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
class GuiceViewProvider extends ViewProviderBase {

    private final Map<VaadinSession, Map<UI, Map<String, View>>> viewCache = new WeakHashMap<>();
    private final Map<Class<? extends UI>, Map<String, Class<? extends View>>> viewMap = new ConcurrentHashMap<>();

    GuiceViewProvider(GuiceVaadinServlet guiceVaadinServlet) {
        super(guiceVaadinServlet);

        for (Class<? extends UI> uiClass : guiceVaadinServlet.getUiClasses()) {

            Map<String, Class<? extends View>> uiSpecificViewMap = guiceVaadinServlet
                    .getViewClasses()
                    .stream()
                    .filter(vc -> guiceVaadinServlet.appliesForUI(uiClass, vc))
                    .collect(
                            toMap(
                                    vc -> vc.getAnnotation(GuiceView.class).value().toLowerCase(),
                                    vc -> vc
                            )
                    );

            viewMap.put(uiClass, uiSpecificViewMap);
        }
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

        final UI currentUI = checkNotNull(UI.getCurrent());

        final Map<String, Class<? extends View>> uiSpecificViewMap = viewMap.get(currentUI.getClass());

        //if no view is registered under this name, null is to be returned
        return uiSpecificViewMap.containsKey(viewName) ? viewName : null;
    }

    @Override
    public View getView(String viewName) {

        final VaadinSession vaadinSession = checkNotNull(
            VaadinSession.getCurrent(),
            "VaadinSession is not set up yet"
        );

        final Map<UI, Map<String, View>> uiToViewsMap = viewCache.computeIfAbsent(vaadinSession, s -> new HashMap<>());

        final UI ui = checkNotNull(UI.getCurrent(), "current UI is not set up yet");

        final Map<String, View> viewMap = uiToViewsMap.computeIfAbsent(ui, u -> new HashMap<>());

        return viewMap.computeIfAbsent(viewName, vn -> createView(ui, viewName));
    }

    private View createView(UI ui, String viewName) {
        final Map<String, Class<? extends View>> uiSpecificViewMap = checkNotNull(this.viewMap.get(ui.getClass()));

        final Class<? extends View> viewClass = checkNotNull(uiSpecificViewMap.get(viewName));

        return getView(viewClass);
    }
}
