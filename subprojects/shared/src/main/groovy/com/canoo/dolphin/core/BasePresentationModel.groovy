package com.canoo.dolphin.core

/**
 * A BasePresentationModel (PM) is an non-empty, unmodifiable collection of {@link BaseAttribute}s.
 * This allows to bind against PMs (i.e. their Attributes) without the need for GRASP-like
 * PresentationModelSwitches.
 * PMs are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

class BasePresentationModel implements PresentationModel {
    protected List<Attribute> attributes = new LinkedList<Attribute>().asSynchronized()
    final String id
    String presentationModelType

    /** @throws AssertionError if the list of attributes is null or empty  **/
    BasePresentationModel(List<BaseAttribute> attributes) {
        this(null, attributes)
    }
    
    /** @throws AssertionError if the list of attributes is null or empty  **/
    BasePresentationModel(String id, List<? extends BaseAttribute> attributes) {
        this.id = id ?: makeId(this)
        this.attributes.addAll(attributes)
    }

    /** @return the immutable internal representation */
    List<Attribute> getAttributes() {
        attributes
    }
    
    protected static String makeId(BasePresentationModel instance) {
        System.identityHashCode(instance).toString()
    }

    Attribute findAttributeByPropertyName(String propertyName) {
        attributes.find { it.propertyName == propertyName }
    }

    Attribute findAttributeByDataId(String dataId) {
        attributes.find { it.dataId == dataId }
    }

    Attribute findAttributeById(long id) {
        attributes.find { it.id == id }
    }

    def propertyMissing(String propName) {
        def result = findAttributeByPropertyName(propName)
        if (null == result) throw new MissingPropertyException("The presentation model doesn't understand '$propName'. Known attributes are ${attributes*.propertyName}", propName, this.getClass())
        return result
    }

    void syncWith(PresentationModel sourcePm ) {
        attributes.each { Attribute targetAttribute ->
            Attribute sourceAttribute = sourcePm.findAttributeByPropertyName(targetAttribute.propertyName)
            if(sourceAttribute) targetAttribute.syncWith sourceAttribute
        }
    }
}