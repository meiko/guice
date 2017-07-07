package com.vaadin.guice.server;

import com.google.inject.Provider;

import com.vaadin.server.VaadinSession;

class VaadinSessionScoper extends ScoperBase<VaadinSessionScoper.SingletonObject> {

    VaadinSessionScoper(Provider<VaadinSession> vaadinSessionProvider) {
        super(() -> SingletonObject.INSTANCE, vaadinSessionProvider);
    }

    static final class SingletonObject{
        private SingletonObject(){
        }

        static final SingletonObject INSTANCE = new SingletonObject();
    }
}