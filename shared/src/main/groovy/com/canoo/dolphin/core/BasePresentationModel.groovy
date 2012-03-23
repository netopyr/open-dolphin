package com.canoo.dolphin.core

/**
 * A BasePresentationModel (PM) is an non-empty, unmodifiable collection of {@link BaseAttribute}s.
 * This allows to bind against PMs (i.e. their Attributes) without the need for GRASP-like
 * PresentationModelSwitches.
 * PMs are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

class BasePresentationModel {
    protected List<BaseAttribute> attributes = new LinkedList<BaseAttribute>().asSynchronized()
    final String id

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
    List<BaseAttribute> getAttributes() {
        attributes
    }
    
    protected static String makeId(BasePresentationModel instance) {
        System.identityHashCode(instance).toString()
    }

    def propertyMissing(String propName) {
        def result = attributes.find { it.propertyName == propName }
        if (null == result) throw new MissingPropertyException("The presentation model doesn't understand '$propName'. Known attributes are ${attributes*.propertyName}", propName, this.getClass())
        return result
    }

    void syncWith(BasePresentationModel sourcePm ) {
        sourcePm.attributes.each { sourceAttribute ->
            def attribute = attributes.find { it.propertyName == sourceAttribute.propertyName }
            if (attribute.id == sourceAttribute.id) return
            attribute.syncWith sourceAttribute
        }
    }
}