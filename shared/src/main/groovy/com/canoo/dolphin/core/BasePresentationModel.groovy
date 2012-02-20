package com.canoo.dolphin.core

/**
 * A BasePresentationModel (PM) is an non-empty, unmodifiable collection of {@link BaseAttribute}s.
 * These Attributes may be backed by different beans and even beans of different types.
 * For convenience, PMs provide a method to change the backing beans of their Attributes.
 * This allows to bind against PMs (i.e. their Attributes) without the need for GRASP-like
 * PresentationModelSwitches.
 * PMs are not meant to be extended for the normal use.
 */

abstract class BasePresentationModel {

    protected List<BaseAttribute> attributes = new LinkedList<BaseAttribute>()

    /** @throws AssertionError if the list of attributes is null or empty  **/
    BasePresentationModel(List<BaseAttribute> attributes) {
        assert attributes
        this.attributes.addAll(attributes)
        this.attributes = this.attributes.asImmutable()
    }

    /** @return the immutable internal representation */
    List<BaseAttribute> getAttributes() {
        attributes
    }

    /** Goes through all attributes and changes their bean to the newBean where it was the oldBean.
     * If oldBean == null, it relies on the type of newBean to find appropriate Attributes.
     * If both are null, do nothing.
     */
    void changeBean(oldBean, newBean) {
        if (null == oldBean && null == newBean) return // yoda says: nothing to do
        def select
        if (oldBean) {
            select = { areBeansTheSame(it, oldBean) }
        } else {
            select = { isTypeApplicable(it, newBean) }
        }
        for (attribute in attributes) {
            if (select(attribute)) attribute.bean = newBean
        }
    }

    void applyBean(newBean){
        changeBean null, newBean
    }

    protected abstract boolean isTypeApplicable(BaseAttribute attribute, def newBean)

    protected abstract boolean areBeansTheSame(BaseAttribute attribute, def oldBean)

}




