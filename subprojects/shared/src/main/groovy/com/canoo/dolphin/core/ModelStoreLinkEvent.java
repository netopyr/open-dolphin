/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.core;

public class ModelStoreLinkEvent {
    public enum EventType {
        ADDED, REMOVED
    }

    private final EventType eventType;
    private final PresentationModel start;
    private final PresentationModel end;
    private final String linkType;

    public ModelStoreLinkEvent(EventType eventType, PresentationModel start, PresentationModel end, String linkType) {
        this.eventType = eventType;
        this.start = start;
        this.end = end;
        this.linkType = linkType;
    }

    public PresentationModel getStart() {
        return start;
    }

    public PresentationModel getEnd() {
        return end;
    }

    public String getLinkType() {
        return linkType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String toString() {
        return new StringBuilder()
                .append("LINK ")
                .append(eventType == EventType.ADDED ? "ADDED" : "REMOVED")
                .append(" start:")
                .append(start.getId())
                .append(" end:")
                .append(end.getId())
                .append(" ")
                .append(linkType)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelStoreLinkEvent linkEvent = (ModelStoreLinkEvent) o;

        if (end != null ? !end.equals(linkEvent.end) : linkEvent.end != null) return false;
        if (eventType != linkEvent.eventType) return false;
        if (linkType != null ? !linkType.equals(linkEvent.linkType) : linkEvent.linkType != null) return false;
        if (start != null ? !start.equals(linkEvent.start) : linkEvent.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (linkType != null ? linkType.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        return result;
    }
}
