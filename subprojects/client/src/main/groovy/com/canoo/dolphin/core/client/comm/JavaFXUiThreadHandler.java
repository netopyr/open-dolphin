package com.canoo.dolphin.core.client.comm;

import com.canoo.dolphin.core.client.comm.UiThreadHandler;
import javafx.application.Platform;

// todo dk: remove dependency to JavaFX

public class JavaFXUiThreadHandler implements UiThreadHandler {
    @Override
    public void executeInsideUiThread(Runnable runnable) {
        Platform.runLater(runnable);
    }
}
