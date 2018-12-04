package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class UIScope implements Scope {

    private final Map<VaadinSession, Map<UI, Map<Key<?>, Object>>> scopesBySession = new WeakHashMap<>();
    private Map<Key<?>, Object> initializationScopeSet;
    private Class<? extends UI> currentlyCreatedUIClass;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        return () -> {
            final VaadinSession vaadinSession = checkNotNull(
                VaadinSession.getCurrent(),
                "VaadinSession is not set up yet."
            );

            Map<UI, Map<Key<?>, Object>> uisToScopedObjects = scopesBySession.computeIfAbsent(vaadinSession, session -> new WeakHashMap<>());

            final Map<Key<?>, Object> scopedObjects = getScopedObjects(uisToScopedObjects);
            
            if(scopedObjects.containsValue(key)) {
                return (T) scopedObjects.get(key);
            }
            
            T result = provider.get();
            scopedObjects.put(key, result);
            return result;
        };
    }

    private Map<Key<?>, Object> getScopedObjects(Map<UI, Map<Key<?>, Object>> uisToScopedObjects) {
        if (initializationScopeSet != null) {
            return initializationScopeSet;
        } else {
            final UI currentUI = checkNotNull(UI.getCurrent(), "current UI is not set up yet");

            return checkNotNull(uisToScopedObjects.get(currentUI));
        }
    }

    Class<? extends UI> currentlyCreatedUIClass() {
        return currentlyCreatedUIClass;
    }

    void startScopeInit(Class<? extends UI> currentlyCreatedUIClass) {
        checkState(initializationScopeSet == null);
        initializationScopeSet = new HashMap<>();
        this.currentlyCreatedUIClass = currentlyCreatedUIClass;
    }

    void flushInitialScopeSet(UI ui) {
        checkNotNull(ui);
        checkState(initializationScopeSet != null);
        checkArgument(ui.getClass().equals(currentlyCreatedUIClass));

        final Map<UI, Map<Key<?>, Object>> uiToScopedObjects = scopesBySession.computeIfAbsent(VaadinSession.getCurrent(), session -> new WeakHashMap<>());

        checkState(uiToScopedObjects.put(ui, initializationScopeSet) == null);
    }

    void endScopeInit() {
        initializationScopeSet = null;
        currentlyCreatedUIClass = null;
    }
}
