package com.vaadin.guice.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.vaadin.guice.annotation.GuiceUI;
import com.vaadin.guice.annotation.GuiceView;
import com.vaadin.guice.annotation.UIScope;
import com.vaadin.guice.annotation.VaadinSessionScope;
import com.vaadin.guice.annotation.ViewScope;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

class VaadinModule extends AbstractModule {

    private final GuiceVaadinServlet GuiceVaadinServlet;

    VaadinModule(GuiceVaadinServlet GuiceVaadinServlet) {
        this.GuiceVaadinServlet = GuiceVaadinServlet;
    }

    @Override
    protected void configure() {
        bindScope(UIScope.class, GuiceVaadinServlet.getUiScoper());
        bindScope(GuiceUI.class, GuiceVaadinServlet.getUiScoper());
        bindScope(ViewScope.class, GuiceVaadinServlet.getViewScoper());
        bindScope(GuiceView.class, GuiceVaadinServlet.getUiScoper());
        bindScope(VaadinSessionScope.class, GuiceVaadinServlet.getVaadinSessionScoper());
        bind(UIProvider.class).toInstance(GuiceVaadinServlet.getGuiceUIProvider());
        bind(ViewProvider.class).toInstance(GuiceVaadinServlet.getViewProvider());

        bind(VaadinSession.class).toProvider(GuiceVaadinServlet.getVaadinSessionProvider());
        bind(UI.class).toProvider(GuiceVaadinServlet.getCurrentUIProvider());
        bind(VaadinService.class).toProvider(GuiceVaadinServlet.getVaadinServiceProvider());

        final Multibinder<View> viewMultibinder = newSetBinder(binder(), View.class);

        GuiceVaadinServlet.getViews().forEach(view -> viewMultibinder.addBinding().to(view));

        UIProvisionListener uiProvisionListener = new UIProvisionListener(GuiceVaadinServlet);

        bindListener(uiProvisionListener, uiProvisionListener);
    }
}
