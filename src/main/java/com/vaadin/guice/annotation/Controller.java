package com.vaadin.guice.annotation;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Classes annotated with controller will be created with every UI and in the Scope
 * of the created UI, regardless whether they get injected somewhere or not. This
 * is useful for 'controller'-classes in the MVC-Pattern that typically are not part
 * of the injection-graph but need to be instantiated with a UI.
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@ScopeAnnotation
public @interface Controller {
}