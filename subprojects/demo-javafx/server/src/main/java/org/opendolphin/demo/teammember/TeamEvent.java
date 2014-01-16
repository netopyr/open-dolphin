package org.opendolphin.demo.teammember;

import org.opendolphin.core.server.DTO;

public class TeamEvent {

    public String type, qualifier;
    public Object value;
    public DTO    dto;

    public TeamEvent(String type, DTO dto) {
        this.type = type;
        this.dto = dto;
    }

    public TeamEvent(String type, String qualifier, Object value) {
        this.type      = type;
        this.qualifier = qualifier;
        this.value     = value;
    }
}
