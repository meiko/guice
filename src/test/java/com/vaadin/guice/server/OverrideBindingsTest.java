package com.vaadin.guice.server;

import com.google.common.collect.ImmutableList;
import com.google.inject.ConfigurationException;

import com.vaadin.guice.testClasses.ASecondImplementation;
import com.vaadin.guice.testClasses.AnImplementation;
import com.vaadin.guice.testClasses.AnInterface;
import com.vaadin.guice.testClasses.AnotherInterface;
import com.vaadin.guice.testClasses.AnotherInterfaceImplementation;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OverrideBindingsTest {

    @Test
    public void dynamically_loaded_modules_should_override() throws NoSuchFieldException, IllegalAccessException {
        GuiceVaadin guiceVaadin = getGuiceVaadin(new VaadinServletWithStaticAndDynamicLoadedModules());

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertTrue(anInterface instanceof ASecondImplementation);

        AnotherInterface anotherInterface = guiceVaadin.getInjector().getInstance(AnotherInterface.class);

        assertNotNull(anotherInterface);
        assertTrue(anotherInterface instanceof AnotherInterfaceImplementation);
    }

    @Test
    public void statically_loaded_modules_should_be_considered() throws NoSuchFieldException, IllegalAccessException {
        GuiceVaadin guiceVaadin = getGuiceVaadin(new VaadinServletWithStaticLoadedModule());

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertThat(anInterface, instanceOf(AnImplementation.class));

        AnotherInterface anotherInterface = guiceVaadin.getInjector().getInstance(AnotherInterface.class);

        assertNotNull(anotherInterface);
        assertThat(anotherInterface, instanceOf(AnotherInterfaceImplementation.class));
    }

    @Test
    public void dynamically_loaded_modules_should_be_considered() throws NoSuchFieldException, IllegalAccessException {
        GuiceVaadin guiceVaadin = getGuiceVaadin(new VaadinServletWithDynamicLoadedModule());

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertTrue(anInterface instanceof ASecondImplementation);
    }

    @Test(expected = ConfigurationException.class)
    public void unbound_classes_should_not_be_available() throws NoSuchFieldException, IllegalAccessException {
        GuiceVaadin guiceVaadin = getGuiceVaadin(new VaadinServletWithDynamicLoadedModule());

        guiceVaadin.getInjector().getInstance(AnotherInterface.class);
    }

    private GuiceVaadin getGuiceVaadin(GuiceVaadinServlet servlet) throws NoSuchFieldException, IllegalAccessException {
        final Field field = servlet.getClass().getSuperclass().getDeclaredField("guiceVaadin");
        field.setAccessible(true);
        return (GuiceVaadin) field.get(servlet);
    }

    private static class VaadinServletWithStaticAndDynamicLoadedModules extends GuiceVaadinServlet {
        @Override
        protected Iterable<String> packagesToScan() {
            return ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.override", "com.vaadin.guice.nonoverride");
        }
    }

    private static class VaadinServletWithStaticLoadedModule extends GuiceVaadinServlet {
        @Override
        protected Iterable<String> packagesToScan() {
            return ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.nonoverride");
        }
    }

    private static class VaadinServletWithDynamicLoadedModule extends GuiceVaadinServlet {
        @Override
        protected Iterable<String> packagesToScan() {
            return ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.override");
        }
    }
}
