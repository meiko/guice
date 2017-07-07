package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class ScoperBase<SCOPE_BASE> implements Scope {
    private final Provider<VaadinSession> vaadinSessionProvider;
    private final Provider<SCOPE_BASE> currentInstanceProvider;
    private final Map<VaadinSession, Map<SCOPE_BASE, Map<Key<?>, Object>>> sessionToScopedObjectsMap = new ConcurrentHashMap<>();

    ScoperBase(Provider<SCOPE_BASE> currentInstanceProvider, Provider<VaadinSession> vaadinSessionProvider) {
        this.currentInstanceProvider = currentInstanceProvider;
        this.vaadinSessionProvider = vaadinSessionProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return () -> {
            Map<SCOPE_BASE, Map<Key<?>, Object>> scopedObjectsByInstance = sessionToScopedObjectsMap.computeIfAbsent(
                    vaadinSessionProvider.get(),
                    s -> new HashMap<>()
            );

            final Map<Key<?>, Object> scopedObjects = scopedObjectsByInstance.computeIfAbsent(
                    currentInstanceProvider.get(),
                    i -> new HashMap<>()
            );

            return (T) scopedObjects.computeIfAbsent(key, k -> unscoped.get());
        };
    }
}
