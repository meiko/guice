package com.vaadin.guice.server;

import com.google.inject.Provider;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

class UIScope extends ScopeBase<UI> {

    UIScope(Provider<VaadinSession> vaadinSessionProvider, Provider<UI> currentUIProvider) {
        super(currentUIProvider, vaadinSessionProvider);
    }

}
