package org.opendolphin.core;

import org.opendolphin.StringUtil;

public class ModelStoreListenerWrapper<U extends Attribute, T extends PresentationModel<U>> implements ModelStoreListener<U, T> {
    private static final String ANY_PRESENTATION_MODEL_TYPE = "*";
    private final String presentationModelType;
    private final ModelStoreListener delegate;

    ModelStoreListenerWrapper(String presentationModelType, ModelStoreListener<U, T> delegate) {
        this.presentationModelType = !StringUtil.isBlank(presentationModelType) ? presentationModelType : ANY_PRESENTATION_MODEL_TYPE;
        this.delegate = delegate;
    }

    private boolean presentationModelTypeMatches(String presentationModelType) {
        return ANY_PRESENTATION_MODEL_TYPE.equals(this.presentationModelType) || this.presentationModelType.equals(presentationModelType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o) return false;

        if (o instanceof ModelStoreListenerWrapper) {
            ModelStoreListenerWrapper that = (ModelStoreListenerWrapper) o;
            return delegate.equals(that.delegate) && presentationModelType.equals(that.presentationModelType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = presentationModelType.hashCode();
        result = 31 * result + delegate.hashCode();
        return result;
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent<U, T> event) {
        String pmType = event.getPresentationModel().getPresentationModelType();
        if (presentationModelTypeMatches(pmType)) {
            delegate.modelStoreChanged(event);
        }
    }
}
