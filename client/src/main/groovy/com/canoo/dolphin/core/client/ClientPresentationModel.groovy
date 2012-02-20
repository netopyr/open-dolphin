package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.BasePresentationModel

// todo dk: remember why we want to support different impls on client and server context
class ClientPresentationModel extends BasePresentationModel{

    ClientPresentationModel(List<BaseAttribute> attributes) {
        super(attributes)
    }
    
    protected boolean isTypeApplicable(BaseAttribute attribute, def newBean) {
        attribute.beanType.isAssignableFrom(newBean.getClass()) // no null check on client side
    }

    protected boolean areBeansTheSame(BaseAttribute attribute, def oldBean) {
        attribute.bean == oldBean // falls back to identity if no equals is defined
    }
}
