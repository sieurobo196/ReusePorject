/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.csb.auth;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

/**
 *
 * @author ChinhNQ
 */
public class MsalesHttpSessionListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof HttpSessionCreatedEvent) {
            HttpSessionCreatedEvent session = (HttpSessionCreatedEvent) event;
            System.out.println("==== Session is created ====");
            System.err.println("ID: " + session.getSession().getId());
        } else if (event instanceof HttpSessionDestroyedEvent) {
            HttpSessionDestroyedEvent session = (HttpSessionDestroyedEvent) event;
            System.out.println("==== Session is destroyed ====");
        }
    }
}
