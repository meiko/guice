package com.vaadin.guice.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to be put on {@link com.vaadin.server.VaadinServiceInitListener}s that are to be
 * automatically detected and configured by guice. Use it like this:
 *
 * <pre>
 * &#064;GuiceVaadinServiceInitListener
 * public class MyVaadinServiceInitListener implements VaadinServiceInitListener {
 *
 *     &#064;Inject
 *     private Logger logger;
 *
 *     void serviceInit(ServiceInitEvent event){
 *         logger.info("the vaadinService is ready now");
 *     }
 * }
 * </pre>
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface GuiceVaadinServiceInitListener {
}
