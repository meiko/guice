package com.vaadin.guice.annotation;

import com.google.inject.BindingAnnotation;

import com.vaadin.navigator.View;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

/**
 * this annotation is to be attached to a field or constructor-parameter that is to be injected by guice
 * and is a {@link Set} of {@link Class}es that extend {@link View}. This set will then contain all
 * classes that implement the View-interface and are navigable from the current UI.
 *
 * <pre>
 * &#064;GuiceUI // will only be attached to MyUI
 * public class MyUi extends UI{
 *
 *    &#064;NavigableViewClasses
 *    private Set&lt;Class&lt;? extends View&gt;&gt; navigableViewClasses;
 *
 *    public void init(VaadinRequest vaadinRequest) {
 *        for(Class&lt;? extends View&gt; viewClass: navigableViewClasses) {
 *            GuiceView annotation = viewClass.getAnnotation(GuiceView.class);
 *
 *            Logger.getGlobal().info(viewClass + " is navigable from the current UI with path " + annotation.value());
 *        }
 *    }
 * }
 * </pre>
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@BindingAnnotation
public @interface NavigableViewClasses {
}