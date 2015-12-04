/*
 * Copyright 2015 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.guice.server;

import com.vaadin.guice.repackaged.UrlPathHelper;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;

import javax.servlet.http.HttpServletRequest;

/**
 * Custom Vaadin servlet request to handle getPathInfo() when the servlet is
 * used via ServletForwardingController which does not set it.
 */
public class GuiceVaadinServletRequest extends VaadinServletRequest {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private boolean clearServletPath;

    /**
     * Construct a guice servlet request
     *
     * @param request
     * @param vaadinService
     * @param clearServletPath
     *            true to use empty string as the servlet path (needed when
     *            using ServletForwardingController for bootstrap page
     *            requests), false to use the normal servlet path - for more
     *            information on this hack, see {@link GuiceVaadinServlet}
     */
    public GuiceVaadinServletRequest(HttpServletRequest request,
                                     VaadinServletService vaadinService, boolean clearServletPath) {
        super(request, vaadinService);
        this.clearServletPath = clearServletPath;
    }

    @Override
    public String getServletPath() {
        if (clearServletPath) {
            // the path where a ServletForwardingController is registered is not
            // a real servlet path
            return "";
        } else {
            return super.getServletPath();
        }
    }

    @Override
    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        if (pathInfo == null) {
            // this uses getServletPath() and should work both with and without
            // clearServletPath
            pathInfo = urlPathHelper.getPathWithinServletMapping(this);
        }
        return pathInfo;
    }

}