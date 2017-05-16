package com.vaadin.guice.annotation;

import com.google.inject.Module;
import com.google.inject.Provider;

import com.vaadin.guice.server.GuiceVaadinServlet;

import org.reflections.Reflections;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Configuration for {@link GuiceVaadinServlet}, attach directly to your GuiceVaadinServlet's
 * declaration like
 * <pre>
 *      <code>
 * {@literal @}Configuration(modules={MyModule.class}, basePackages="com.myproject")
 * {@literal @}WebServlet(urlPatterns = "/*", name = "MyServlet", asyncSupported = true)
 * public static class MyServlet extends GuiceVaadinServlet {
 * }
 *      </code>
 *  </pre>
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * An array of classes for modules to be installed by guice. Each of these classes must have a
     * default ( no-args ) constructor. There are three interfaces that will be recognized when implemented
     * by one of the classes in modules:
     *
     * Every class that implements {@link com.vaadin.guice.server.NeedsInjector} will have it's
     * {@link com.vaadin.guice.server.NeedsInjector#setInjectorProvider(Provider)} method called with a provider
     * for the {@link com.google.inject.Injector} that holds the Guice-context. The {@link Provider} will throw
     * a {@link NullPointerException} on {@link Provider#get()} if the {@link com.google.inject.Injector} is not ready
     * yet.
     *
     * Every class that implements {@link com.vaadin.guice.server.NeedsReflections} will have it's
     * {@link com.vaadin.guice.server.NeedsReflections#setReflections(Reflections)} method called with the
     * {@link Reflections} that are being used to set up the Guice-context.
     *
     */
    Class<? extends Module>[] modules() default {};

    /**
     * A list of packages that is to be scanned for the guice-context. Sub-packages are included as
     * well.
     */
    String[] basePackages();
}
