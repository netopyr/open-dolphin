package org.opendolphin.demo.team;

import org.opendolphin.core.server.DTO;

public class TeamEvent {

    enum Type { NEW, CHANGE, REBASE, REMOVE, RELEASE }

    public Type type;
    public String qualifier;
    public Object value;
    public DTO    dto;

    public TeamEvent(Type type, DTO dto) {
        this.type = type;
        this.dto = dto;
    }

    public TeamEvent(Type type, String qualifier, Object value) {
        this.type      = type;
        this.qualifier = qualifier;
        this.value     = value;
    }
}

