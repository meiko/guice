package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

class VaadinSessionScope implements Scope {

    private final Map<VaadinSession, Map<Key<?>, Object>> scopeMapsBySession = new WeakHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        return () -> {
            final Map<Key<?>, Object> scopeMap = scopeMapsBySession.computeIfAbsent(VaadinSession.getCurrent(), v -> new HashMap<>());

            return (T) scopeMap.computeIfAbsent(key, k -> provider.get());
        };
    }
}