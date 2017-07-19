package com.vaadin.guice.server;

import com.google.inject.Provider;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;

class ViewScope extends ScopeBase<View> {

    ViewScope(Provider<VaadinSession> vaadinSessionProvider, Provider<View> currentViewProvider) {
        super(currentViewProvider, vaadinSessionProvider);
    }
}
