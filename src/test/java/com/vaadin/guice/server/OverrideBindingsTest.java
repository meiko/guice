package com.vaadin.guice.server;

import com.google.common.collect.ImmutableList;
import com.google.inject.ConfigurationException;

import com.vaadin.guice.testClasses.ASecondImplementation;
import com.vaadin.guice.testClasses.AnImplementation;
import com.vaadin.guice.testClasses.AnInterface;
import com.vaadin.guice.testClasses.AnotherInterface;
import com.vaadin.guice.testClasses.AnotherInterfaceImplementation;

import org.junit.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OverrideBindingsTest {

    @Test
    public void dynamically_loaded_modules_should_override() throws ReflectiveOperationException {
        GuiceVaadin guiceVaadin = new GuiceVaadin(new Reflections(ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.override", "com.vaadin.guice.nonoverride")), new Annotation[0]);

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertTrue(anInterface instanceof ASecondImplementation);

        AnotherInterface anotherInterface = guiceVaadin.getInjector().getInstance(AnotherInterface.class);

        assertNotNull(anotherInterface);
        assertTrue(anotherInterface instanceof AnotherInterfaceImplementation);
    }

    @Test
    public void statically_loaded_modules_should_be_considered() throws ReflectiveOperationException {
        GuiceVaadin guiceVaadin = new GuiceVaadin(new Reflections(ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.nonoverride")), new Annotation[0]);

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertThat(anInterface, instanceOf(AnImplementation.class));

        AnotherInterface anotherInterface = guiceVaadin.getInjector().getInstance(AnotherInterface.class);

        assertNotNull(anotherInterface);
        assertThat(anotherInterface, instanceOf(AnotherInterfaceImplementation.class));
    }

    @Test
    public void dynamically_loaded_modules_should_be_considered() throws ReflectiveOperationException {
        GuiceVaadin guiceVaadin = new GuiceVaadin(new Reflections(ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.override")), new Annotation[0]);

        AnInterface anInterface = guiceVaadin.getInjector().getInstance(AnInterface.class);

        assertNotNull(anInterface);
        assertTrue(anInterface instanceof ASecondImplementation);
    }

    @Test(expected = ConfigurationException.class)
    public void unbound_classes_should_not_be_available() throws ReflectiveOperationException {
        GuiceVaadin guiceVaadin = new GuiceVaadin(new Reflections(ImmutableList.of("com.vaadin.guice.testClasses", "com.vaadin.guice.override")), new Annotation[0]);

        guiceVaadin.getInjector().getInstance(AnotherInterface.class);
    }
}
