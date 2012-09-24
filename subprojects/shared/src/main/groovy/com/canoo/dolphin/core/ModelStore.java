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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModelStore {

    // todo dk: this doesn't need to be ConcurrentHashMaps since we are thread-confined
    private final Map<String, PresentationModel> presentationModels = new ConcurrentHashMap<String, PresentationModel>();
    private final Map<String, List<PresentationModel>> modelsPerType = new ConcurrentHashMap<String, List<PresentationModel>>();
    private final Map<Long, Attribute> attributesPerId = new ConcurrentHashMap<Long, Attribute>();
    private final Map<String, List<Attribute>> attributesPerQualifier = new ConcurrentHashMap<String, List<Attribute>>();
    private final LinkStore linkStore = new LinkStore();

    private final PropertyChangeListener ATTRIBUTE_WORKER = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            Attribute attribute = (Attribute) event.getSource();
            String oldQualifier = (String) event.getOldValue();
            String newQualifier = (String) event.getNewValue();

            if (null != oldQualifier) removeAttributeByQualifier(attribute, oldQualifier);
            if (null != newQualifier) addAttributeByQualifier(attribute);
        }
    };

    public Set<String> listPresentationModelIds() {
        return Collections.unmodifiableSet(presentationModels.keySet());
    }

    public Collection<PresentationModel> listPresentationModels() {
        return Collections.unmodifiableCollection(presentationModels.values());
    }

    public boolean add(PresentationModel model) {
        if (null == model) return false;

        if (presentationModels.containsKey(model.getId())) {
            throw new IllegalArgumentException("There already is a PM with id " + model.getId());
        }
        boolean added = false;
        if (!presentationModels.containsValue(model)) {
            presentationModels.put(model.getId(), model);
            addPresentationModelByType(model);
            for (Attribute attribute : model.getAttributes()) {
                addAttributeById(attribute);
                attribute.addPropertyChangeListener(Attribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
                if (!isBlank(attribute.getQualifier())) addAttributeByQualifier(attribute);
            }
            added = true;
        }
        return added;
    }

    public boolean remove(PresentationModel model) {
        if (null == model) return false;
        boolean removed = false;
        if (presentationModels.containsValue(model)) {
            unlink(model);
            removePresentationModelByType(model);
            presentationModels.remove(model.getId());
            for (Attribute attribute : model.getAttributes()) {
                removeAttributeById(attribute);
                attribute.removePropertyChangeListener(Attribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
                if (!isBlank(attribute.getQualifier())) removeAttributeByQualifier(attribute);
            }
            removed = true;
        }
        return removed;
    }

    protected void addAttributeById(Attribute attribute) {
        if (null == attribute || attributesPerId.containsKey(attribute.getId())) return;
        attributesPerId.put(attribute.getId(), attribute);
    }

    protected void removeAttributeById(Attribute attribute) {
        if (null == attribute) return;
        attributesPerId.remove(attribute.getId());
    }

    protected void addAttributeByQualifier(Attribute attribute) {
        if (null == attribute) return;
        String qualifier = attribute.getQualifier();
        if (isBlank(qualifier)) return;
        List<Attribute> list = attributesPerQualifier.get(qualifier);
        if (null == list) {
            list = new ArrayList<Attribute>();
            attributesPerQualifier.put(qualifier, list);
        }
        if (!list.contains(attribute)) list.add(attribute);
    }

    protected void removeAttributeByQualifier(Attribute attribute) {
        if (null == attribute) return;
        String qualifier = attribute.getQualifier();
        if (isBlank(qualifier)) return;
        List<Attribute> list = attributesPerQualifier.get(qualifier);
        if (null != list) {
            list.remove(attribute);
        }
    }

    protected void addPresentationModelByType(PresentationModel model) {
        if (null == model) return;
        String type = model.getPresentationModelType();
        if (isBlank(type)) return;
        List<PresentationModel> list = modelsPerType.get(type);
        if (null == list) {
            list = new ArrayList<PresentationModel>();
            modelsPerType.put(type, list);
        }
        if (!list.contains(model)) list.add(model);
    }

    protected void removePresentationModelByType(PresentationModel model) {
        if (null == model) return;
        String type = model.getPresentationModelType();
        if (isBlank(type)) return;
        List<PresentationModel> list = modelsPerType.get(type);
        if (null != list) {
            list.remove(model);
        }
    }

    protected void removeAttributeByQualifier(Attribute attribute, String qualifier) {
        if (isBlank(qualifier)) return;
        List<Attribute> list = attributesPerQualifier.get(qualifier);
        if (null == list) return;
        list.remove(attribute);
    }

    public PresentationModel findPresentationModelById(String id) {
        return presentationModels.get(id);
    }

    public List<PresentationModel> findAllPresentationModelsByType(String type) {
        if (isBlank(type) || !modelsPerType.containsKey(type)) return Collections.emptyList();
        return Collections.unmodifiableList(modelsPerType.get(type));
    }

    public boolean containsPresentationModel(PresentationModel model) {
        return model != null && presentationModels.containsKey(model.getId());
    }

    public boolean containsPresentationModel(String id) {
        return presentationModels.containsKey(id);
    }

    public Attribute findAttributeById(long id) {
        return attributesPerId.get(id);
    }

    public List<Attribute> findAllAttributesByQualifier(String qualifier) {
        if (isBlank(qualifier) || !attributesPerQualifier.containsKey(qualifier)) return Collections.emptyList();
        return Collections.unmodifiableList(attributesPerQualifier.get(qualifier));
    }

    public void registerAttribute(Attribute attribute) {
        if (null == attribute) return;
        boolean listeningAlready = false;
        for (PropertyChangeListener listener : attribute.getPropertyChangeListeners(Attribute.QUALIFIER_PROPERTY)) {
            if (ATTRIBUTE_WORKER == listener) {
                listeningAlready = true;
                break;
            }
        }

        if (!listeningAlready) {
            attribute.addPropertyChangeListener(Attribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
        }

        addAttributeByQualifier(attribute);
        addAttributeById(attribute);
    }

    private boolean isBlank(String str) {
        return null == str || str.trim().length() == 0;
    }

    public Link link(PresentationModel a, PresentationModel b, String type) {
        if (null == type || !containsPresentationModel(a) || !containsPresentationModel(b)) {
            return null;
        }
        BaseLink link = new BaseLink(a, b, type);
        Link existingLink = linkStore.findLinkByExample(link);
        if (null != existingLink) return existingLink;
        linkStore.add(link);
        return link;
    }

    public void unlink(Link link) {
        if (null != link &&
                containsPresentationModel(link.getStart()) &&
                containsPresentationModel(link.getEnd())) {
            linkStore.remove(link);
        }
    }

    private void unlink(PresentationModel model) {
        if (containsPresentationModel(model)) linkStore.removeAllLinks(model);
    }

    public List<Link> findAllLinksByModelAndType(PresentationModel model, String type) {
        return null != type || containsPresentationModel(model) ? linkStore.findLinksByType(model, type) : Collections.<Link>emptyList();
    }

    public List<Link> findAllLinksByModel(PresentationModel model) {
        return containsPresentationModel(model) ? linkStore.findAllLinksByModel(model) : Collections.<Link>emptyList();
    }

    public boolean linkExists(PresentationModel a, PresentationModel b, String type) {
        return null != type &&
                containsPresentationModel(a) &&
                containsPresentationModel(b) &&
                null != linkStore.findLinkByExample(new BaseLink(a, b, type));
    }

    public boolean linkExists(Link link) {
        return null != link &&
                containsPresentationModel(link.getStart()) &&
                containsPresentationModel(link.getEnd()) &&
                null != linkStore.findLinkByExample(link);
    }

    private static class LinkStore {
        private final Map<PresentationModel, List<Link>> links = new ConcurrentHashMap<PresentationModel, List<Link>>();

        public boolean add(Link link) {
            List<Link> startList = links.get(link.getStart());

            if (null == startList) {
                startList = new ArrayList<Link>();
                links.put(link.getStart(), startList);
            }

            if (startList.contains(link)) return false;
            startList.add(link);

            // a link could point to the same node, if so just add this link once
            if (link.getStart().equals(link.getEnd())) return true;

            List<Link> endList = links.get(link.getEnd());

            if (null == endList) {
                endList = new ArrayList<Link>();
                links.put(link.getEnd(), endList);
            }

            endList.add(link);

            return true;
        }

        public boolean remove(Link link) {
            List<Link> startList = links.get(link.getStart());

            if (null == startList || !startList.contains(link)) return false;
            startList.remove(link);

            if (link.getStart().equals(link.getEnd())) return true;

            // a link could point to the same node, if so just remove this link once
            List<Link> endList = links.get(link.getEnd());

            if (null != endList) {
                endList.remove(link);
            }

            return true;
        }

        public Link findLinkByExample(Link example) {
            PresentationModel start = example.getStart();

            List<Link> list = links.get(start);
            if (null == list || list.isEmpty()) return null;

            for (Link link : list) {
                if (linkTypesAreEqual(link, example) &&
                        link.getEnd().equals(example.getEnd())) {
                    return link;
                }
            }

            return null;
        }

        public List<Link> findLinksByType(PresentationModel model, String type) {
            List<Link> list = links.get(model);
            if (null == list) return Collections.emptyList();
            List<Link> linksByType = new ArrayList<Link>();
            for (Link link : list) {
                if (linkTypesAreEqual(link.getType(), type)) {
                    linksByType.add(link);
                }
            }
            return Collections.unmodifiableList(linksByType);
        }

        public List<Link> findAllLinksByModel(PresentationModel model) {
            List<Link> list = links.get(model);
            if (null == list) return Collections.emptyList();
            return Collections.unmodifiableList(list);
        }

        private boolean linkTypesAreEqual(Link a, Link b) {
            return linkTypesAreEqual(a.getType(), b.getType());
        }

        private boolean linkTypesAreEqual(String a, String b) {
            return a.equals(b);
        }

        public void removeAllLinks(PresentationModel model) {
            List<Link> list = links.get(model);
            if (null == list) return;
            for (Link link : list) {
                List<Link> otherList = links.get(link.getEnd());
                if (null == otherList) continue;
                otherList.remove(link);
            }
        }
    }
}
