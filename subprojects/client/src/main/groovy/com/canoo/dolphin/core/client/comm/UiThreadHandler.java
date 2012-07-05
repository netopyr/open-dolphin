package com.canoo.dolphin.core.client.comm;

public interface UiThreadHandler {
    void executeInsideUiThread(Runnable runnable);
}
