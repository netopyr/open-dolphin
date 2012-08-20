package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel

public class JsonCodecTest extends GroovyTestCase {

    void testEmpty() {
        assertSoManyCommands(0)
    }

    void testOne() {
        assertSoManyCommands(1)
    }

    void testMany() {
        assertSoManyCommands(10)
    }

    void assertSoManyCommands(int count) {
        def codec = new JsonCodec()
        def commands = []
        count.times{
            commands << new AttributeCreatedCommand(pmId: it, attributeId: it*count, propertyName: "prop$it", newValue: "value$it", qualifier: null)
        }
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString() == decoded.toString()
    }

    void testCreatedPmCommand() {
        def codec = new JsonCodec()
        def commands = [new CreatePresentationModelCommand(pmId: 1, pmType: null, attributes: [] )]
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString() == decoded.toString()
    }


}
