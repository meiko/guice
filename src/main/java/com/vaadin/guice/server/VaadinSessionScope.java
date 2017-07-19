package com.vaadin.guice.server;

import com.google.inject.Provider;
import com.vaadin.server.VaadinSession;

class VaadinSessionScope extends ScopeBase<VaadinSessionScope.SingletonObject> {

    VaadinSessionScope(Provider<VaadinSession> vaadinSessionProvider) {
        super(() -> SingletonObject.INSTANCE, vaadinSessionProvider);
    }

    static final class SingletonObject {
        static final SingletonObject INSTANCE = new SingletonObject();

        private SingletonObject() {
        }
    }
}