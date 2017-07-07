package com.vaadin.guice.server;

import com.google.inject.Injector;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.UI;

import org.reflections.Reflections;

import javax.servlet.ServletException;

import static com.google.common.collect.Iterables.toArray;

/**
 * Subclass of the standard {@link com.vaadin.server.VaadinServlet Vaadin servlet} that adds a
 * {@link GuiceUIProvider} to every new Vaadin session
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 */
public abstract class GuiceVaadinServlet extends VaadinServlet {

    private final GuiceVaadin guiceVaadin;

    public GuiceVaadinServlet() {

        final String[] packagesToScan = toArray(packagesToScan(), String.class);

        Reflections reflections = new Reflections((Object[]) packagesToScan);

        try {
            this.guiceVaadin = new GuiceVaadin(reflections, getClass().getAnnotations());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return an Iterable of Strings that are the packages that are to be scanned for {@link UI}s,
     * {@link com.vaadin.navigator.View}s, {@link com.vaadin.ui.Component}s, {@link
     * com.google.inject.Module}s and so forth.
     */
    protected abstract Iterable<String> packagesToScan();

    @Override
    protected void servletInitialized() throws ServletException {
        VaadinService.getCurrent().addSessionInitListener(guiceVaadin);
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        return new GuiceVaadinServletService(this, deploymentConfiguration, guiceVaadin);
    }

    /**
     * can be made public in subclasses to expose the injector and get access to the guice-context
     * from another servlet for example.
     *
     * @return the {@link Injector that holds the guice-context}
     */
    @SuppressWarnings("unused")
    protected Injector getInjector() {
        return guiceVaadin.getInjector();
    }
}
