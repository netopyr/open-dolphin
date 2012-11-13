package com.canoo.dolphin.core.client.comm;

import com.canoo.dolphin.core.client.ClientPresentationModel;

import java.util.List;
import java.util.Map;

public class OnFinishedHandlerAdapter implements OnFinishedHandler {
    @Override
    public void onFinished(List<ClientPresentationModel> presentationModels) {
        // do nothing
    }

    @Override
    public void onFinishedData(List<Map> data) {
        // do nothing
    }
}
