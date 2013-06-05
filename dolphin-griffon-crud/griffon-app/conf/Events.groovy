import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler
import org.opendolphin.core.comm.JsonCodec

onBootstrapEnd = { app ->
    ClientDolphin dolphin = new ClientDolphin()
    dolphin.setClientModelStore(new ClientModelStore(dolphin))
    String url = System.properties.remote ?: 'http://localhost:8080/dolphin-grails/dolphin/'
    ClientConnector connector = new HttpClientConnector(dolphin, url)
    connector.codec = new JsonCodec()
    connector.uiThreadHandler = new JavaFXUiThreadHandler()
    dolphin.clientConnector = connector
    app.bindings.dolphin = dolphin
}