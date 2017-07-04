package com.vaadin.guice.server;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServiceInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

import java.util.Iterator;

import static com.google.common.collect.Iterators.concat;

public class GuiceVaadinServletService extends VaadinServletService{

    private final GuiceVaadin guiceVaadin;

    public GuiceVaadinServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration, GuiceVaadin guiceVaadin) throws ServiceException {
        super(servlet, deploymentConfiguration);
        this.guiceVaadin = guiceVaadin;
    }

    @Override
    protected Iterator<VaadinServiceInitListener> getServiceInitListeners() {
        return concat(super.getServiceInitListeners(), guiceVaadin.getServiceInitListeners());
    }
}
