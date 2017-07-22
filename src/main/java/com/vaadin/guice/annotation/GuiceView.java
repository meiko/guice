package com.vaadin.guice.annotation;

import com.google.inject.ScopeAnnotation;

import com.vaadin.navigator.View;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to be placed on {@link com.vaadin.navigator.View}-classes that should be handled by
 * the {@link com.vaadin.navigator.ViewProvider}.
 * <p>
 * <pre>
 * &#064;GuiceView(&quot;&quot;)
 * public class MyDefaultView extends CustomComponent implements View {
 *     // ...
 * }
 * </pre>
 *
 * @author Petter Holmström (petter@vaadin.com)
 * @author Henri Sara (hesara@vaadin.com)
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@ScopeAnnotation
public @interface GuiceView {

    /**
     * The name of the view. This is the name that the view is registered with when calling {@link
     * com.vaadin.navigator.Navigator#addView(String, View)}.
     */
    String value();
}
