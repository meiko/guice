package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class UIScope implements Scope {

    private final Map<VaadinSession, Map<UI, Map<Key<?>, Object>>> scopesBySession = new WeakHashMap<>();
    private Map<Key<?>, Object> initializationScopeSet;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        return () -> {
            Map<UI, Map<Key<?>, Object>> uisToScopedObjects = scopesBySession.computeIfAbsent(VaadinSession.getCurrent(), session -> new WeakHashMap<>());

            final Map<Key<?>, Object> scopedObjects = initializationScopeSet != null
                    ? initializationScopeSet
                    : checkNotNull(uisToScopedObjects.get(checkNotNull(UI.getCurrent())));

            return (T) scopedObjects.computeIfAbsent(key, k -> provider.get());
        };
    }

    void startScopeInit(){
        checkState(initializationScopeSet == null);
        initializationScopeSet = new HashMap<>();
    }

    void flushInitialScopeSet(UI ui){
        checkNotNull(ui);
        checkState(initializationScopeSet != null);

        final Map<UI, Map<Key<?>, Object>> uiToScopedObjects = scopesBySession.computeIfAbsent(VaadinSession.getCurrent(), session -> new WeakHashMap<>());

        checkState(uiToScopedObjects.put(ui, initializationScopeSet) == null);
    }

    void endScopeInit(){
        initializationScopeSet = null;
    }
}
