package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class ViewScope implements Scope {

    private final Map<VaadinSession, Map<UI, Map<View, Map<Key<?>, Object>>>> scopesBySession = new WeakHashMap<>();
    private Map<Key<?>, Object> initializationScopeSet;
    private Class<? extends View> currentlyCreatedViewClass;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        return () -> {
            Map<Key<?>, Object> scopeMap = initializationScopeSet != null
                    ? initializationScopeSet
                    : getScopeMap();

            return (T) scopeMap.computeIfAbsent(key, k -> provider.get());
        };
    }

    private Map<Key<?>, Object> getScopeMap() {
        final UI currentUI = checkNotNull(UI.getCurrent());

        final Map<View, Map<Key<?>, Object>> viewScopeMap = getViewsToScopesMap(currentUI);

        final Navigator navigator = checkNotNull(currentUI.getNavigator());

        final View currentView = checkNotNull(navigator.getCurrentView());

        return viewScopeMap.computeIfAbsent(currentView, cv -> new HashMap<>());
    }

    private Map<View, Map<Key<?>, Object>> getViewsToScopesMap(UI currentUI) {
        final VaadinSession vaadinSession = checkNotNull(VaadinSession.getCurrent());

        Map<UI, Map<View, Map<Key<?>, Object>>> uisToScopedViews = scopesBySession.computeIfAbsent(vaadinSession, session -> new WeakHashMap<>());

        return uisToScopedViews.computeIfAbsent(currentUI, ui -> new HashMap<>());
    }

    void startScopeInit(Class<? extends View> currentlyCreatedViewClass) {
        checkState(initializationScopeSet == null);
        initializationScopeSet = new HashMap<>();
        this.currentlyCreatedViewClass = currentlyCreatedViewClass;
    }

    void flushInitialScopeSet(View view) {
        checkNotNull(view);
        checkState(initializationScopeSet != null);
        checkArgument(view.getClass().equals(currentlyCreatedViewClass));

        final UI currentUI = checkNotNull(UI.getCurrent());

        final Map<View, Map<Key<?>, Object>> viewsToScopesMap = getViewsToScopesMap(currentUI);

        checkState(viewsToScopesMap.put(view, initializationScopeSet) == null);
    }

    void endScopeInit() {
        initializationScopeSet = null;
        currentlyCreatedViewClass = null;
    }
}
