package com.vaadin.guice.annotation;

import com.google.inject.Module;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.RequestHandler;
import com.vaadin.ui.UI;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface PackagesToScan {

    /**
     * A list of all packages that should be scanned for {@link UI}s, {@link View}s, {@link
     * ViewChangeListener}s, {@link com.vaadin.server.BootstrapListener}s,
     * {@link RequestHandler}s and {@link Module}s.
     */
    String[] value();
}