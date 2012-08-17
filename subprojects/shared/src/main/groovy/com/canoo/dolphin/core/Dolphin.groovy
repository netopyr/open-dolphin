package com.canoo.dolphin.core

abstract class Dolphin {
    abstract ModelStore getModelStore()

    public Set<String> listPresentationModelIds() {
        modelStore.listPresentationModelIds()
    }
    public Collection<PresentationModel> listPresentationModels() {
        modelStore.listPresentationModels()
    }

    public List<PresentationModel> findAllPresentationModelsByType(String presentationModelType) {
        modelStore.findAllPresentationModelsByType(presentationModelType)
    }

    public PresentationModel findPresentationModelById(String id) {
        modelStore.findPresentationModelById(id)
    }
}
