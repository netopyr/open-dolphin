package org.opendolphin.server.adapter

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class InvalidationServletSpec extends Specification {

    boolean invalidateWasReached = false

    void "with 'invalidate' in the path info, the session should be invalidated"() {
        given:
        def (servlet, req, resp) = mockServlet(url)

        when:
        servlet.doPost(req, resp)

        then:
        invalidateWasReached == outcome

        where:
        url                   | outcome
        "whatever/invalidate" | true
        "whatever/no-inval"   | false
    }


    def mockServlet(String url) {

        def servlet = new InvalidationServlet()
        def session = [
            invalidate : { -> invalidateWasReached = true }
        ] as HttpSession
        def req = [
            getSession:     { -> session },
            getRequestURL:  { -> new StringBuffer(url) }
        ] as HttpServletRequest
        def resp = [
            getWriter:   { -> new MockPrintWriter() }
        ] as HttpServletResponse
        return [ servlet, req, resp ]
    }

}