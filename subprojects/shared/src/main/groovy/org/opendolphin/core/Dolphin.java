package org.opendolphin.core;

import groovy.lang.Closure;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by hendrikebbers on 20.01.15.
 */
public interface Dolphin<U extends Attribute, T extends PresentationModel<U>> {

    boolean add(T model);

    boolean remove(T model);

    U findAttributeById(String id);

    List<U> findAllAttributesByQualifier(String qualifier);

    Set<String> listPresentationModelIds();

    Collection<T> listPresentationModels();

    List<T> findAllPresentationModelsByType(String presentationModelType);

    T getAt(String id);

    T findPresentationModelById(String id);

    void removeModelStoreListener(ModelStoreListener listener);

    void removeModelStoreListener(String presentationModelType, ModelStoreListener listener);

    boolean hasModelStoreListener(ModelStoreListener listener);

    void addModelStoreListener(String presentationModelType, ModelStoreListener listener);

    void addModelStoreListener(String presentationModelType, Closure listener);

    boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener);

    void addModelStoreListener(ModelStoreListener listener);

    void addModelStoreListener(Closure listener);

    void updateQualifiers(T presentationModel);
}
