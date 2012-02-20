package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.BasePresentationModel

class ServerPresentationModel extends BasePresentationModel{

    ServerPresentationModel(List<BaseAttribute> attributes) {
        super(attributes)
    }

    protected boolean isTypeApplicable(BaseAttribute attribute, def newBean) {
        attribute.bean == null && attribute.beanType.isAssignableFrom(newBean.getClass())
    }

    protected boolean areBeansTheSame(BaseAttribute attribute, def oldBean) {
        oldBean.is(attribute.bean)
    }
}
