package com.canoo.dolphin.core.client.comm;

import com.canoo.dolphin.core.client.ClientPresentationModel;

import java.util.List;

public interface OnFinishedHandler {
    public void onFinished(List<ClientPresentationModel> presentationModels);
}
