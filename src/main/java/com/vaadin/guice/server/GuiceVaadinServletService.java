package com.vaadin.guice.server;

import com.vaadin.server.*;

import java.util.Iterator;

import static com.google.common.collect.Iterators.concat;

class GuiceVaadinServletService extends VaadinServletService {

    private final GuiceVaadinServlet guiceVaadinServlet;

    GuiceVaadinServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration, GuiceVaadinServlet guiceVaadinServlet) throws ServiceException {
        super(servlet, deploymentConfiguration);
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    protected Iterator<VaadinServiceInitListener> getServiceInitListeners() {
        return concat(super.getServiceInitListeners(), guiceVaadinServlet.getServiceInitListeners());
    }
}
