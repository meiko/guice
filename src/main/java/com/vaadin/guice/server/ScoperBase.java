package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;

abstract class ScoperBase<SCOPE_BASE> implements Scope, SessionDestroyListener, SessionInitListener {
    private final Provider<VaadinSession> vaadinSessionProvider;
    private final Provider<SCOPE_BASE> currentInstanceProvider;
    private final Map<VaadinSession, Map<SCOPE_BASE, Map<Key<?>, Object>>> sessionToScopedObjectsMap = new ConcurrentHashMap<>();
    private Map<Key<?>, Object> currentInitializationScopeSet = null;

    ScoperBase(Provider<SCOPE_BASE> currentInstanceProvider, Provider<VaadinSession> vaadinSessionProvider) {
        this.currentInstanceProvider = currentInstanceProvider;
        this.vaadinSessionProvider = vaadinSessionProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return () -> (T) getCurrentScopeMap().computeIfAbsent(key, k -> unscoped.get());
    }

    private Map<Key<?>, Object> getCurrentScopeMap() {

        final Map<SCOPE_BASE, Map<Key<?>, Object>> scopedObjectsByInstance = sessionToScopedObjectsMap.get(vaadinSessionProvider.get());

        checkState(scopedObjectsByInstance != null);

        return scopedObjectsByInstance.computeIfAbsent(currentInstanceProvider.get(), key -> new HashMap<>());
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        final Map<SCOPE_BASE, Map<Key<?>, Object>> map = sessionToScopedObjectsMap.remove(event.getSession());

        checkState(map != null);

        for (Map<Key<?>, Object> keyObjectMap : map.values()) {
            KeyObjectMapPool.returnMap(keyObjectMap);
        }
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        sessionToScopedObjectsMap.put(event.getSession(), new HashMap<>());
    }
}
