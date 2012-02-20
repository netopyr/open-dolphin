package com.canoo.dolphin.binding;


import com.canoo.dolphin.core.TestBean;


import static com.canoo.dolphin.binding.Binder.bind
import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.BasePresentationModel;

public class BinderTests extends GroovyTestCase {

    void testHappyPath() {
        given:
        def att = new BaseAttribute(TestBean, "name")
        def pm = new PresentationModel([att])
        def initialValue = "Andres&Dierk"
        def srcBean = new TestBean(name: initialValue)
        att.bean = srcBean

        def targetBean = new TestBean()
        assert targetBean.name == null

        when:
        bind "name" of pm to "name" of targetBean

        assert targetBean.name == initialValue

        def newValue = "newValue"
        att.value = newValue

        then:
        assert targetBean.name == newValue
    }

}

class PresentationModel extends BasePresentationModel {
    PresentationModel(List<BaseAttribute> attributes) {
        super(attributes)
    }

    @Override
    protected boolean areBeansTheSame(BaseAttribute attribute, Object oldBean) {
        return false
    }

    @Override
    protected boolean isTypeApplicable(BaseAttribute attribute, Object newBean) {
        return false
    }
}
