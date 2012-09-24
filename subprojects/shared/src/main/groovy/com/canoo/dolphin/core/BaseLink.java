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

public class BaseLink implements Link {
    private final String type;
    private final PresentationModel start;
    private final PresentationModel end;

    public BaseLink(PresentationModel start, PresentationModel end, String type) {
        if (null == start) throw new IllegalArgumentException("Link start cannot be null");
        if (null == end) throw new IllegalArgumentException("Link end cannot be null");
        if (isBlank(type)) throw new IllegalArgumentException("Link type cannot be null");
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public PresentationModel getStart() {
        return start;
    }

    public PresentationModel getEnd() {
        return end;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseLink baseLink = (BaseLink) o;

        if (end != null ? !end.equals(baseLink.end) : baseLink.end != null) return false;
        if (start != null ? !start.equals(baseLink.start) : baseLink.start != null) return false;
        if (type != null ? !type.equals(baseLink.type) : baseLink.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new StringBuilder()
                .append("LINK start:")
                .append(start.getId())
                .append(" end:")
                .append(end.getId())
                .append(" ")
                .append(type)
                .toString();
    }

    // todo: aa this method belongs to an utility class
    private static boolean isBlank(String str) {
        return null == str || str.trim().length() == 0;
    }
}
