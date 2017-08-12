package com.vaadin.guice.annotation;

import com.google.inject.BindingAnnotation;

import com.vaadin.navigator.View;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

/**
 * this annotation is to be attached to a field or constructor-parameter that is being injected by
 * guice and is a {@link Set} of {@link Class}es that extend {@link View} or a {@link java.util.Map}
 * of Classes that extend View and {@link String}s.
 *
 * Set's will contain all classes that extend View and are navigable from the current UI.
 *
 * Map's will map these classes to the URI-fragment they are registered under.
 *
 * A view-class is navigable if it is contained in the scanned packages, and is not restricted to
 * other UIs with {@link ForUI}.
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 * @see PackagesToScan
 * @see ForUI
 *
 * <pre>
 * &#064;GuiceUI // will only be attached to MyUI
 * public class MyUi extends UI{
 *
 *    //classes only
 *    &#064;NavigableViewClasses
 *    private Set&lt;Class&lt;? extends View&gt;&gt; set;
 *
 *    //classes and URI-fragments
 *    &#064;NavigableViewClasses
 *    private Map&lt;Class&lt;? extends View&gt;, String&gt; map;
 *
 *    public void init(VaadinRequest vaadinRequest) {
 *        for(Entry&lt;Class&lt;? extends View&gt,String&gt; entry: ) {
 *
 *              Class&lt;? extends View&gt viewClass = entry.getKey();
 *              String uriFragment = entry.getValue();
 *
 *            Logger.getGlobal().info(viewClass + " is navigable from the current UI with
 * URI-fragment " +
 * uriFragment);
 *        }
 *    }
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@BindingAnnotation
public @interface NavigableViewClasses {
}