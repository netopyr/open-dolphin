package org.opendolphin.core.server;

import org.opendolphin.core.ModelStore;
import org.opendolphin.core.ModelStoreConfig;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;

import java.util.List;

public class ServerModelStore extends ModelStore {

    protected List<Command> currentResponse = null;
    protected long pmInstanceCount = 0L;

    public ServerModelStore() {
    }

    public ServerModelStore(ModelStoreConfig config) {
        super(config);
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    protected List<Command> getCurrentResponse() {
        return currentResponse;
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    protected void setCurrentResponse(List<Command> currentResponse) {
        this.currentResponse = currentResponse;
    }

    @Override
    public boolean add(PresentationModel model) {
        boolean added = super.add(model);
        if (! added) return added;
        ((ServerPresentationModel)model).modelStore = this;
        return true;
    }
}
