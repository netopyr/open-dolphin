package org.opendolphin.core;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;

/**
 * A model store that does not store, i.e. neither adds nor removes presentation models.
 * It uses almost no memory.
 * Useful for a second channel like for long-polling that shell not store any presentation models.
 * */

public class NoModelStore extends ClientModelStore {

    public NoModelStore(ClientDolphin clientDolphin) {
        super(clientDolphin);
    }

    @Override
    public boolean add(PresentationModel model) {
        return false;
    }

    @Override
    public boolean remove(PresentationModel model) {
        return false;
    }
}
