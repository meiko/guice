package com.vaadin.guice.server;

public class ViewProviderTest {
/*
    private GuiceViewProvider viewProvider;

    @Before
    @SuppressWarnings("unckecked")
    public void setup() throws ReflectiveOperationException {

        Provider<VaadinSession> vaadinSessionProvider = (Provider<VaadinSession>) mock(Provider.class);
        Provider<UI> currentUIProvider = (Provider<UI>) mock(Provider.class);
        Provider<View> currentViewProvider = (Provider<View>) mock(Provider.class);
        Provider<VaadinService> vaadinServiceProvider = (Provider<VaadinService>) mock(Provider.class);

        Reflections reflections = new Reflections("com.vaadin.guice.testClasses");

        final GuiceVaadinServlet guiceVaadinServlet = new GuiceVaadinServlet(vaadinSessionProvider,
                currentUIProvider,
                currentViewProvider,
                vaadinServiceProvider,
                reflections,
                new Annotation[0]);

        viewProvider = guiceVaadinServlet.getViewProvider();
    }

    @Test
    public void view_provider_get_view_name() throws ServiceException, NoSuchFieldException, IllegalAccessException {
        assertEquals(viewProvider.getViewName("view0"), "view0");
        assertEquals(viewProvider.getViewName("viewa/id1"), "viewa");
        assertEquals(viewProvider.getViewName("viewaa/id2"), "viewaa");
        assertEquals(viewProvider.getViewName("viewaaa/id3"), "viewaaa");
        assertEquals(viewProvider.getViewName("viewb"), "viewb");
        // getViewName() must return null if the view name is not handled by the view provider
        assertNull(viewProvider.getViewName("viewc"));
    }
*/
}
