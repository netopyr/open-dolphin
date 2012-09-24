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

    /**
     * Returns a {@code Set} of all known presentation model ids.<br/>
     * Never returns null. The returned {@code Set} is immutable.
     *
     * @return a {@code} Set of all ids of all presentation models contained in this store.
     */
    public Set<String> listPresentationModelIds() {
        return Collections.unmodifiableSet(presentationModels.keySet());
    }

    /**
     * Returns a {@code Collection} of all presentation models found in this store.<br/>
     * Never returns empty. The returned {@code Collection} is immutable.
     *
     * @return a {@code Collection} of all presentation models found in this store.
     */
    public Collection<PresentationModel> listPresentationModels() {
        return Collections.unmodifiableCollection(presentationModels.values());
    }

    /**
     * Adds a presentation model to this store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     */
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

    /**
     * Removes a presentation model from this store.<br/>
     * This method will also unlink the model from every relationship it may belong to.
     *
     * @param model the model to be removed from the store.
     * @return if the remove operation was successful or not.
     */
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

    /**
     * Find a presentation model by the given id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search
     * @return a presentation model instance of there's an id match, {@code null} otherwise.
     */
    public PresentationModel findPresentationModelById(String id) {
        return presentationModels.get(id);
    }

    /**
     * Finds all presentation models that share the same type.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param type the tpye to search for
     * @return a {@code List} of all presentation models for which there was a match in their type.
     */
    public List<PresentationModel> findAllPresentationModelsByType(String type) {
        if (isBlank(type) || !modelsPerType.containsKey(type)) return Collections.emptyList();
        return Collections.unmodifiableList(modelsPerType.get(type));
    }

    /**
     * Finds out if a model is contained in this store.
     *
     * @param model the model to search in the store.
     * @return true if the model is found in this store, false otherwise.
     */
    public boolean containsPresentationModel(PresentationModel model) {
        return model != null && presentationModels.containsKey(model.getId());
    }

    /**
     * Finds out if a model is contained in this store, based on its id.
     *
     * @param id the id to search in the store.
     * @return true if the model is found in this store, false otherwise.
     */
    public boolean containsPresentationModel(String id) {
        return presentationModels.containsKey(id);
    }

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return an attribute whose id matches the parameter, {@code null} otherwise.
     */
    public Attribute findAttributeById(long id) {
        return attributesPerId.get(id);
    }

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns empty. The returned {@code List} is immutable.
     *
     * @return a {@code List} of all attributes fo which their qualifier was a match.
     */
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

    /**
     * Establishes a link between two presentation models.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if any argument is {@code null}
     * or if any of the models are not contained in the store.<br/>
     *
     * @param start the starting model
     * @param end   the ending model
     * @param type  the type of relationship, i.e, "PARENT_CHILD".
     * @return a link between both models.
     */
    public Link link(PresentationModel start, PresentationModel end, String type) {
        if (null == type || !containsPresentationModel(start) || !containsPresentationModel(end)) {
            return null;
        }
        BaseLink link = new BaseLink(start, end, type);
        Link existingLink = linkStore.findLinkByExample(link);
        if (null != existingLink) return existingLink;
        linkStore.add(link);
        return link;
    }

    /**
     * Severs the link between two presentation models as long as the link exists with the given arguments.<br/>
     *
     * @param start the starting model
     * @param end   the ending model
     * @param type  the type of relationship, i.e, "PARENT_CHILD".
     * @return true if such a link existed and was removed successfully, false otherwise.
     */
    public boolean unlink(PresentationModel start, PresentationModel end, String type) {
        if (null == type || !containsPresentationModel(start) || !containsPresentationModel(end)) {
            return false;
        }
        return linkStore.remove(new BaseLink(start, end, type));
    }

    /**
     * Severs the link between two presentation models as long as the link exists with the given arguments.<br/>
     *
     * @param link the link to removed.
     * @return true if such a link existed and was removed successfully, false otherwise.
     */
    public boolean unlink(Link link) {
        return null != link &&
                containsPresentationModel(link.getStart()) &&
                containsPresentationModel(link.getEnd()) &&
                linkStore.remove(link);
    }

    protected boolean unlink(PresentationModel model) {
        if (containsPresentationModel(model) && !linkStore.findAllLinksByModel(model, Link.Direction.OUTGOING).isEmpty()) {
            linkStore.removeAllLinks(model);
            return true;
        }
        return false;
    }

    /**
     * Finds out of there's a link of the given type between two presentation models.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if any argument is {@code null}
     * or if any of the models are not contained in the store.<br/>
     *
     * @param start the starting model
     * @param end   the ending model
     * @param type  the type of relationship, i.e, "PARENT_CHILD".
     * @return a link that matches the passed-in parameters, {@code null} otherwise.
     */
    public Link findLink(PresentationModel start, PresentationModel end, String type) {
        if (null == type || !containsPresentationModel(start) || !containsPresentationModel(end)) {
            return null;
        }
        BaseLink link = new BaseLink(start, end, type);
        return linkStore.findLinkByExample(link);
    }

    /**
     * Finds all links of the given type where the model participates.<br/>
     * The model may be either a starting or ending model.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param model a starting or ending model.
     * @param type  the type of link to search for.
     * @return a {@code List} of all links where the model and type are found.
     */
    public List<Link> findAllLinksByModelAndType(PresentationModel model, String type) {
        return findAllLinksByModelAndType(model, type, Link.Direction.BOTH);
    }

    /**
     * Finds all links of the given type where the model participates.<br/>
     * The model should be found at a specific position in the link, given by the specified direction.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param model     a starting or ending model given a specific direction.
     * @param type      the type of link to search for.
     * @param direction the direction of the link. {@code Link.Direction.BOTH} will be used if the argument is null.
     * @return a {@code List} of all links where the model and type are found.
     */
    public List<Link> findAllLinksByModelAndType(PresentationModel model, String type, Link.Direction direction) {
        return null != type || containsPresentationModel(model) ? linkStore.findLinksByType(model, type, direction) : Collections.<Link>emptyList();
    }

    /**
     * Finds all links (not caring for a particular type) where the given model participates.<br/>
     * The model may be either a starting or ending model.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param model a starting or ending model.
     * @return a {@code List} of all links where the model is found.
     */
    public List<Link> findAllLinksByModel(PresentationModel model) {
        return findAllLinksByModel(model, Link.Direction.BOTH);
    }

    /**
     * Finds all links (not caring for a particular type) where the given model participates.<br/>
     * The model should be found at a specific position in the link, given by the specified direction.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param model     a starting or ending model.
     * @param direction the direction of the link. {@code Link.Direction.BOTH} will be used if the argument is null.
     * @return a {@code List} of all links where the model is found.
     */
    public List<Link> findAllLinksByModel(PresentationModel model, Link.Direction direction) {
        return containsPresentationModel(model) ? linkStore.findAllLinksByModel(model, direction) : Collections.<Link>emptyList();
    }

    /**
     * Finds ouf if a link of the given type exists between two presentation models.<br/>
     *
     * @param start the starting model
     * @param end   the ending model
     * @param type  the type of relationship, i.e, "PARENT_CHILD".
     * @return true if such a link exists, false otherwise.
     */
    public boolean linkExists(PresentationModel start, PresentationModel end, String type) {
        return null != type &&
                containsPresentationModel(start) &&
                containsPresentationModel(end) &&
                null != linkStore.findLinkByExample(new BaseLink(start, end, type));
    }

    /**
     * Finds ouf if a link of the given type exists between two presentation models.<br/>
     *
     * @param link the link to search for.
     * @return true if such a link exists, false otherwise.
     */
    public boolean linkExists(Link link) {
        return null != link &&
                containsPresentationModel(link.getStart()) &&
                containsPresentationModel(link.getEnd()) &&
                null != linkStore.findLinkByExample(link);
    }

    private static class LinkStore {
        private class LinkBox {
            private final List<Link> incoming = new ArrayList<Link>();
            private final List<Link> outgoing = new ArrayList<Link>();

            private List<Link> all() {
                List<Link> all = new ArrayList<Link>();
                all.addAll(outgoing);
                for (Link link : incoming) {
                    if (!all.contains(link)) all.add(link);
                }
                return all;
            }
        }

        private final Map<PresentationModel, LinkBox> LINKS = new ConcurrentHashMap<PresentationModel, LinkBox>();

        public boolean add(Link link) {
            LinkBox links = LINKS.get(link.getStart());

            if (null == links) {
                links = new LinkBox();
                LINKS.put(link.getStart(), links);
            }

            if (links.outgoing.contains(link)) return false;
            links.outgoing.add(link);

            links = LINKS.get(link.getEnd());
            if (null == links) {
                links = new LinkBox();
                LINKS.put(link.getEnd(), links);
            }

            if (links.incoming.contains(link)) return false;
            links.incoming.add(link);

            return true;
        }

        public boolean remove(Link link) {
            boolean removed = false;
            LinkBox links = LINKS.get(link.getStart());

            if (null != links && links.outgoing.contains(link)) {
                links.outgoing.remove(link);
                removed = true;
            }

            links = LINKS.get(link.getEnd());

            if (null != links && links.incoming.contains(link)) {
                links.incoming.remove(link);
                removed = true;
            }

            return removed;
        }

        public Link findLinkByExample(Link example) {
            PresentationModel start = example.getStart();

            LinkBox links = LINKS.get(start);
            if (null == links) return null;

            for (Link link : links.outgoing) {
                if (linkTypesAreEqual(link, example) &&
                        link.getEnd().equals(example.getEnd())) {
                    return link;
                }
            }

            return null;
        }

        public List<Link> findLinksByType(PresentationModel model, String type, Link.Direction direction) {
            LinkBox links = LINKS.get(model);
            if (null == links) return Collections.emptyList();
            List<Link> linksByType = new ArrayList<Link>();
            if (null == direction || direction == Link.Direction.BOTH || direction == Link.Direction.OUTGOING) {
                for (Link link : links.outgoing) {
                    if (linkTypesAreEqual(link.getType(), type)) {
                        linksByType.add(link);
                    }
                }
            }
            if (null == direction || direction == Link.Direction.BOTH || direction == Link.Direction.INCOMING) {

                for (Link link : links.incoming) {
                    if (linkTypesAreEqual(link.getType(), type) && !links.incoming.contains(link)) {
                        linksByType.add(link);
                    }
                }
            }
            return Collections.unmodifiableList(linksByType);
        }

        public List<Link> findAllLinksByModel(PresentationModel model, Link.Direction direction) {
            LinkBox links = LINKS.get(model);
            if (null == links) return Collections.emptyList();
            switch (direction) {
                case INCOMING:
                    return Collections.unmodifiableList(links.incoming);
                case OUTGOING:
                    return Collections.unmodifiableList(links.outgoing);
                case BOTH:
                default:
                    return Collections.unmodifiableList(links.all());
            }
        }

        private boolean linkTypesAreEqual(Link a, Link b) {
            return linkTypesAreEqual(a.getType(), b.getType());
        }

        private boolean linkTypesAreEqual(String a, String b) {
            return a.equals(b);
        }

        public void removeAllLinks(PresentationModel model) {
            LinkBox links = LINKS.remove(model);
            if (null == links) return;
            for (Link link : links.outgoing) {
                LinkBox otherLinks = LINKS.get(link.getEnd());
                if (null == otherLinks) continue;
                otherLinks.incoming.remove(link);
            }
            for (Link link : links.incoming) {
                LinkBox otherLinks = LINKS.get(link.getStart());
                if (null == otherLinks) continue;
                otherLinks.outgoing.remove(link);
            }
        }
    }
}
