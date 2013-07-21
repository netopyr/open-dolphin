package org.opendolphin.core.client.comm

import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpResponseException
import org.apache.http.entity.StringEntity

class SessionAffinityCheckingResponseHandlerTest extends GroovyTestCase {

    void testHandleResponseNoCookieEverSet() {
        SessionAffinityCheckingResponseHandler handler = new SessionAffinityCheckingResponseHandler()
        handler.throwExceptionOnSessionChange = true
        def response = [
            getStatusLine : { [getStatusCode: {200} ] as StatusLine },
            getEntity : { new StringEntity("some response") },
            getFirstHeader : { null },
        ] as HttpResponse

        // can be done multiple times without exception
        handler.handleResponse(response)
        handler.handleResponse(response)
    }

    void testHandleResponseWithChangingCookies() {
        SessionAffinityCheckingResponseHandler handler = new SessionAffinityCheckingResponseHandler()
        handler.throwExceptionOnSessionChange = true
        def changingValue = "a"
        def response = [
            getStatusLine : { [getStatusCode: {200} ] as StatusLine },
            getEntity : { new StringEntity("some response") },
            getFirstHeader : { name -> [getValue: { changingValue } ] as Header },
        ] as HttpResponse

        // can be done multiple times without exception
        handler.handleResponse(response)
        handler.handleResponse(response)

        changingValue++

        def result = shouldFail(IOException) {
            handler.handleResponse(response)
        }
        assert result =~ /Http session must not change but did. Old: a, new: b/

    }

    void testHandleResponse404() {
        SessionAffinityCheckingResponseHandler handler = new SessionAffinityCheckingResponseHandler()
        handler.throwExceptionOnSessionChange = false
        def response = [
            getStatusLine : {
                [
                    getStatusCode: { 404 },
                    getReasonPhrase: { "File not found" }
                ] as StatusLine
            },
            getEntity : { null },
        ] as HttpResponse

        def result = shouldFail(HttpResponseException) {
            handler.handleResponse(response)
        }
        assert result =~ /File not found/

    }
}
