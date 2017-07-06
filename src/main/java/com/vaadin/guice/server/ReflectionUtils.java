package com.vaadin.guice.server;

import com.google.inject.Module;

import com.vaadin.guice.annotation.ForUI;
import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.Import;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

final class ReflectionUtils {

    private ReflectionUtils() {
    }

    static Set<Module> loadModulesFromAnnotations(Annotation[] annotations, Reflections reflections, GuiceVaadin guiceVaadin) {
        return stream(annotations)
                .map(ReflectionUtils::createIfModuleToCreate)
                .filter(Objects::nonNull)
                .map(module -> postProcess(reflections, guiceVaadin, module))
                .collect(toSet());
    }

    @SuppressWarnings("unchecked")
    private static Module createIfModuleToCreate(Annotation annotation) {

        checkNotNull(annotation);

        final Class<? extends Annotation> annotationClass = annotation.getClass();

        if (!annotationClass.isAnnotationPresent(Import.class)) {
            return null;
        }

        final Import anImport = annotationClass.getAnnotation(Import.class);

        try {
            final Class<? extends Module> moduleClass = anImport.value();

            final Constructor<? extends Module> constructorWithAnnotation = moduleClass.getConstructor(annotationClass);

            if (constructorWithAnnotation != null) {
                constructorWithAnnotation.setAccessible(true);
                return constructorWithAnnotation.newInstance(annotation);
            }

            final Constructor<? extends Module> defaultConstructor = moduleClass.getConstructor();

            if (defaultConstructor != null) {
                defaultConstructor.setAccessible(true);
                return defaultConstructor.newInstance();
            }

            throw new IllegalArgumentException("no suitable constructor found for " + moduleClass);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Module create(Class<? extends Module> type, Reflections reflections, final GuiceVaadin guiceVaadin) {
        final Module module;
        try {
            module = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return postProcess(reflections, guiceVaadin, module);
    }

    private static Module postProcess(Reflections reflections, GuiceVaadin guiceVaadin, Module module) {
        if (module instanceof NeedsReflections) {
            ((NeedsReflections) module).setReflections(reflections);
        }

        if (module instanceof NeedsInjector) {
            ((NeedsInjector) module).setInjectorProvider(guiceVaadin);
        }

        return module;
    }

    @SuppressWarnings("unchecked")
    static Set<Module> loadModulesFromPath(Reflections reflections, GuiceVaadin guiceVaadin, Set<Module> modulesFromAnnotations) throws ReflectiveOperationException {
        Set<Class<? extends Module>> modulesFromAnnotationClasses = modulesFromAnnotations
                .stream()
                .map(Module::getClass)
                .collect(toSet());

        return reflections
                .getSubTypesOf(Module.class)
                .stream()
                .filter(moduleClass -> !modulesFromAnnotationClasses.contains(moduleClass))
                .map(moduleClass -> create(moduleClass, reflections, guiceVaadin))
                .collect(toSet());
    }

    @SuppressWarnings("unchecked")
    static Map<Class<? extends UI>, Set<Class<? extends ViewChangeListener>>> getViewChangeListenerClasses(Reflections reflections, Set<Class<? extends UI>> uiClasses) {

        Map<Class<? extends UI>, Set<Class<? extends ViewChangeListener>>> viewChangeListenersByUI = uiClasses
                .stream()
                .collect(toMap(uiClass -> uiClass, uiClass -> new HashSet<>()));

        for (Class<? extends UI> uiClass : uiClasses) {
            viewChangeListenersByUI.put(uiClass, new HashSet<>());
        }

        for (Class<? extends ViewChangeListener> viewChangeListenerClass : reflections.getSubTypesOf(ViewChangeListener.class)) {

            final ForUI annotation = viewChangeListenerClass.getAnnotation(ForUI.class);

            if (annotation == null) {
                viewChangeListenersByUI.values().forEach(listeners -> listeners.add(viewChangeListenerClass));
            } else {
                checkArgument(annotation.value().length > 0, "ForUI#value must contain one ore more UI-classes");

                for (Class<? extends UI> applicableUiClass : annotation.value()) {
                    final Set<Class<? extends ViewChangeListener>> viewChangeListenersForUI = viewChangeListenersByUI.get(applicableUiClass);

                    checkArgument(
                            viewChangeListenersForUI != null,
                            "%s is listed as applicableUi in the @ForUI-annotation of %s, but is not annotated with @GuiceUI"
                    );

                    final Class<? extends Component> viewContainer = applicableUiClass.getAnnotation(GuiceUI.class).viewContainer();

                    checkArgument(!viewContainer.equals(Component.class), "%s is annotated as @ForUI for %s, however viewContainer() is not set in @GuiceUI");

                    viewChangeListenersForUI.add(viewChangeListenerClass);
                }
            }
        }

        return viewChangeListenersByUI;
    }
}

