package com.vaadin.guice.server;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class UIScope implements Scope {

    private final Map<VaadinSession, Map<GUIID, Map<Key<?>, Object>>> scopesBySession = new WeakHashMap<>();
    private final Map<UI, GUIID> uiToUIID = new WeakHashMap<>();
    private GUIID currentGUIID;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        return () -> {
            Map<GUIID, Map<Key<?>, Object>> uiScopesForCurrentVaadinSession = scopesBySession.computeIfAbsent(VaadinSession.getCurrent(), session -> new WeakHashMap<>());

            final Map<Key<?>, Object> currentUIScopedObjects = uiScopesForCurrentVaadinSession.computeIfAbsent(getCurrentGUIID(), guiid -> new HashMap<>());

            return (T) currentUIScopedObjects.computeIfAbsent(key, k -> provider.get());
        };
    }

    private GUIID getCurrentGUIID() {
        GUIID guiid = currentGUIID;

        if(guiid == null){
            UI ui = checkNotNull(UI.getCurrent());

            guiid = checkNotNull(uiToUIID.get(ui));
        } else {
            checkState(UI.getCurrent() == null);
        }
        return guiid;
    }

    void startScopeInit(){
        checkState(currentGUIID == null);
        currentGUIID = new GUIID();
    }

    void flushInitialScopeSet(UI ui){
        checkNotNull(ui);
        checkState(uiToUIID.put(ui, currentGUIID) == null);
    }

    void endScopeInit(){
        currentGUIID = null;
    }

    private static class GUIID {

        private final UUID uiid = UUID.randomUUID();

        UUID getUiid() {
            return uiid;
        }
    }
}
