package com.canoo.dolphin.core

abstract class Dolphin {
    abstract ModelStore getModelStore()

    public List<PresentationModel> findAllPresentationModelsByType(String presentationModelType) {
        modelStore.findAllPresentationModelsByType(presentationModelType)
    }

    public PresentationModel findPresentationModelById(String id) {
        modelStore.findPresentationModelById(id)
    }
}
