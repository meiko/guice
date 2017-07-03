package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class VaadinSessionScoper implements Scope, SessionDestroyListener {

    private final Map<VaadinSession, Map<Key<?>, Object>> scopedObjectsBySession = new ConcurrentHashMap<>();
    private final Provider<VaadinSession> vaadinSessionProvider;

    VaadinSessionScoper(Provider<VaadinSession> vaadinSessionProvider) {
        this.vaadinSessionProvider = vaadinSessionProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {

        return () -> {
            Map<Key<?>, Object> scopedObjects = getOrCreateScopedObjectsMap();

            T t = (T) scopedObjects.get(key);

            if (t == null) {
                t = unscoped.get();
                scopedObjects.put(key, t);
            }

            return t;
        };
    }

    private Map<Key<?>, Object> getOrCreateScopedObjectsMap() {
        return scopedObjectsBySession.computeIfAbsent(vaadinSessionProvider.get(), k -> KeyObjectMapPool.leaseMap());
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        final Map<Key<?>, Object> map = scopedObjectsBySession.remove(event.getSession());

        if (map != null) {
            KeyObjectMapPool.returnMap(map);
        }
    }
}