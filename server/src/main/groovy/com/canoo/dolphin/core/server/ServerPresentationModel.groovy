package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.BasePresentationModel

class ServerPresentationModel extends BasePresentationModel{

    ServerPresentationModel(List<ServerAttribute> attributes) {
        this(null, attributes)
    }

    ServerPresentationModel(String id, List<ServerAttribute> attributes) {
        super(id, attributes)
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

    protected boolean isTypeApplicable(ServerAttribute attribute, Object newBean) {
        attribute.bean == null && attribute.beanType.isAssignableFrom(newBean.getClass())
    }

    protected boolean areBeansTheSame(ServerAttribute attribute, Object oldBean) {
        oldBean.is(attribute.bean)
    }
}
