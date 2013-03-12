package org.opendolphin.core.client.comm

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage

class JavaFXUiThreadHandlerTests extends GroovyTestCase {
    void testUiThreadHandler() {
        Application.launch(DummyApplication)
    }
}

class DummyApplication extends Application {
    @Override
    void start(Stage stage) throws Exception {
        new JavaFXUiThreadHandler().executeInsideUiThread({})
        Platform.exit()
    }
}
