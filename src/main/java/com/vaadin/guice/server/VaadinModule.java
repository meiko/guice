package com.vaadin.guice.server;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.inject.multibindings.Multibinder.newSetBinder;

class VaadinModule extends AbstractModule {

    private final GuiceVaadinServlet guiceVaadinServlet;
    private final Provider<Injector> injectorProvider;

    VaadinModule(GuiceVaadinServlet GuiceVaadinServlet, Provider<Injector> injectorProvider) {
        this.guiceVaadinServlet = GuiceVaadinServlet;
        this.injectorProvider = injectorProvider;
    }

    @Override
    protected void configure() {
        bindScope(UIScope.class, guiceVaadinServlet.getUiScoper());
        bindScope(ViewScope.class, guiceVaadinServlet.getViewScoper());
        bindScope(GuiceView.class, guiceVaadinServlet.getUiScoper());
        bindScope(GuiceUI.class, guiceVaadinServlet.getVaadinSessionScoper());
        bindScope(VaadinSessionScope.class, guiceVaadinServlet.getVaadinSessionScoper());
        bind(UIProvider.class).toInstance(guiceVaadinServlet.getGuiceUIProvider());
        bind(ViewProvider.class).toInstance(guiceVaadinServlet.getViewProvider());
        bind(VaadinSession.class).toProvider(guiceVaadinServlet.getVaadinSessionProvider());
        bind(UI.class).toProvider(guiceVaadinServlet.getCurrentUIProvider());
        bind(VaadinService.class).toProvider(guiceVaadinServlet.getVaadinServiceProvider());

        for (Class<? extends UI> uiClass : guiceVaadinServlet.getUis()) {
            bindUI(uiClass);
        }

        final Multibinder<View> viewMultibinder = newSetBinder(binder(), View.class);

        guiceVaadinServlet.getViews().forEach(view -> viewMultibinder.addBinding().to(view));

        UIProvisionListener uiProvisionListener = new UIProvisionListener(guiceVaadinServlet);

        bindListener(uiProvisionListener, uiProvisionListener);
    }

    private <T extends UI> void bindUI(Class<T> uiClass){

        try {
            checkArgument(uiClass.getConstructors().length == 1);

            uiClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        bind(uiClass).toProvider(() -> {
            try {
                T ui = uiClass.newInstance();

                UI.setCurrent(ui);

                injectorProvider.get().injectMembers(ui);

                return ui;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
