package com.vaadin.guice.annotation;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.*;

/**
 * This annotation will put elements in guice's 'UI'-scope, so for every {@link com.vaadin.ui.UI}
 * constructed by guice, there is exactly one instance of any given type in the UI-scope.
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Josh Long (josh@joshlong.com)
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ScopeAnnotation
public @interface UIScope {
}
