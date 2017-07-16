package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class ScopeBase<T> implements Scope {
    private final Provider<VaadinSession> vaadinSessionProvider;
    private final Provider<T> currentInstanceProvider;
    private final Map<VaadinSession, Map<T, Map<Key<?>, Object>>> sessionToScopedObjectsMap = new ConcurrentHashMap<>();

    ScopeBase(Provider<T> currentInstanceProvider, Provider<VaadinSession> vaadinSessionProvider) {
        this.currentInstanceProvider = currentInstanceProvider;
        this.vaadinSessionProvider = vaadinSessionProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Provider<U> scope(final Key<U> key, final Provider<U> unscoped) {
        return () -> {
            Map<T, Map<Key<?>, Object>> scopedObjectsByInstance = sessionToScopedObjectsMap.computeIfAbsent(
                    vaadinSessionProvider.get(),
                    s -> new HashMap<>()
            );

            final Map<Key<?>, Object> scopedObjects = scopedObjectsByInstance.computeIfAbsent(
                    currentInstanceProvider.get(),
                    i -> new HashMap<>()
            );

            return (U) scopedObjects.computeIfAbsent(key, k -> unscoped.get());
        };
    }
}
