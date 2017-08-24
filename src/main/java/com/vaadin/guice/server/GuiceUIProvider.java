package com.vaadin.guice.server;

import com.google.inject.Injector;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Vaadin {@link com.vaadin.server.UIProvider} that looks up UI classes from the Guice application
 * context. The UI classes must be annotated with {@link GuiceUI}.
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 * @author Bernd Hopp (bernd@vaadin.com)
 */
class GuiceUIProvider extends UIProvider {

    private final Map<String, Class<? extends UI>> pathToUIMap;
    private final GuiceVaadinServlet guiceVaadinServlet;

    GuiceUIProvider(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
        Logger logger = Logger.getLogger(getClass().getName());

        logger.info("Checking the application context for Vaadin UIs");

        pathToUIMap = new ConcurrentHashMap<>();

        for (Class<? extends UI> uiClass : guiceVaadinServlet.getUiClasses()) {

            GuiceUI annotation = uiClass.getAnnotation(GuiceUI.class);

            checkArgument(annotation != null, "%s needs a GuiceUI-annotation", uiClass);

            String path = annotation.path();

            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            path = path.toLowerCase();

            Class<? extends UI> existingUiForPath = pathToUIMap.get(path);

            checkState(
                    existingUiForPath == null,
                    "[%s] is already mapped to the path [%s]",
                    existingUiForPath,
                    path
            );

            logger.log(Level.INFO, "Mapping Vaadin UI [{0}] to path [{1}]",
                    new Object[]{uiClass.getCanonicalName(), path});

            checkArgument(pathToUIMap.put(path, uiClass) == null, "multiple ui's mapped to the same path %s", path);
        }

        if (pathToUIMap.isEmpty()) {
            logger.log(Level.WARNING, "Found no Vaadin UIs in the application context");
        }
    }

    @Override
    public Class<? extends UI> getUIClass(
            UIClassSelectionEvent uiClassSelectionEvent) {
        String path = getPath(uiClassSelectionEvent);

        return pathToUIMap.get(path);
    }

    private String getPath(UIClassSelectionEvent uiClassSelectionEvent) {
        String path = uiClassSelectionEvent.getRequest().getPathInfo();

        final int indexOfBang = path.indexOf('!');

        if (indexOfBang > -1) {
            path = path.substring(0, indexOfBang);
        } else if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path.toLowerCase();
    }

    @Override
    public synchronized UI createInstance(UICreateEvent event) {
        final UIScope uiScoper = guiceVaadinServlet.getUiScoper();
        final Injector injector = guiceVaadinServlet.getInjector();

        try {
            final Class<? extends UI> uiClass = event.getUIClass();

            uiScoper.startScopeInit(uiClass);

            UI ui = injector.getInstance(uiClass);

            uiScoper.flushInitialScopeSet(ui);

            return ui;
        } finally {
            uiScoper.endScopeInit();
        }
    }
}
