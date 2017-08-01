package com.vaadin.guice.server;

import java.util.UUID;

class GUIID {

    private final UUID uiid;

    GUIID() {
        this.uiid = UUID.randomUUID();
    }

    public UUID getUiid() {
        return uiid;
    }
}
