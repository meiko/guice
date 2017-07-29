package com.vaadin.guice.server;

public abstract class ScopeTestBase {
/*
    GuiceVaadinServlet GuiceVaadinServlet;
    private Provider<VaadinSession> vaadinSessionProvider;

    @Before
    @SuppressWarnings("unckecked")
    public void setup() throws ReflectiveOperationException {
        vaadinSessionProvider = mock(Provider.class);
        Provider<UI> currentUIProvider;
        currentUIProvider = (Provider<UI>) mock(Provider.class);
        Provider<View> currentViewProvider = (Provider<View>) mock(Provider.class);
        Provider<VaadinService> vaadinServiceProvider = (Provider<VaadinService>) mock(Provider.class);

        Reflections reflections = new Reflections("com.vaadin.guice.server.testClasses");

        GuiceVaadinServlet = new GuiceVaadinServlet(
                vaadinSessionProvider,
                currentUIProvider,
                currentViewProvider,
                vaadinServiceProvider,
                reflections,
                new Annotation[0]
        );
    }

    @Test //default prototype behaviour should not be affected
    public void testPrototype() throws ServiceException, NoSuchFieldException, IllegalAccessException {
        newSession();
        Target target1 = GuiceVaadinServlet.getInjector().getInstance(Target.class);
        newSession();
        Target target2 = GuiceVaadinServlet.getInjector().getInstance(Target.class);

        assertNotEquals(target1.getPrototype1(), target2.getPrototype1());
    }

    @Test //default singleton behaviour should not be affected
    public void testSingleton() throws ServiceException, NoSuchFieldException, IllegalAccessException {
        newSession();
        Target target1 = GuiceVaadinServlet.getInjector().getInstance(Target.class);
       newSession();
        Target target2 = GuiceVaadinServlet.getInjector().getInstance(Target.class);

        assertEquals(target1.getSingleton1(), target2.getSingleton1());
    }

    //different transaction-scopes should lead to a different set of transaction-scoped objects
    @Test
    public void testTransactionScopeDifferent() throws ServiceException, NoSuchFieldException, IllegalAccessException {

        newSession();
        Target target1 = GuiceVaadinServlet.getInjector().getInstance(Target.class);
        newSession();
        Target target2 = GuiceVaadinServlet.getInjector().getInstance(Target.class);

        assertNotNull(target1);
        assertNotNull(target2);
    }

    //a single transaction-scope should lead to the same set of transaction-scoped objects being injected
    @Test
    public void testTransactionScopeSame() throws ServiceException, NoSuchFieldException, IllegalAccessException {

        newSession();
        Target target1 = GuiceVaadinServlet.getInjector().getInstance(Target.class);
        newSession();
        Target target2 = GuiceVaadinServlet.getInjector().getInstance(Target.class);

        assertNotNull(target1);
        assertNotNull(target2);

        assertNotEquals(target1.getUiScoped1(), target2.getUiScoped1());
        assertNotEquals(target1.getUiScoped2(), target2.getUiScoped2());
        assertNotEquals(target1.getUiScoped1().getUiScoped2(), target2.getUiScoped1().getUiScoped2());
    }

    void setVaadinSession(VaadinSession vaadinSession) {
        when(vaadinSessionProvider.get()).thenReturn(vaadinSession);
    }

    VaadinSession newSession() throws ServiceException {
        VaadinSession vaadinSession = mock(VaadinSession.class);

        SessionInitEvent sessionInitEvent = mock(SessionInitEvent.class);

        setVaadinSession(vaadinSession);
        when(sessionInitEvent.getSession()).thenReturn(vaadinSession);

        GuiceVaadinServlet.sessionInit(sessionInitEvent);

        return vaadinSession;
    }
    */
}
