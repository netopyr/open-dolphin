package com.canoo.dolphin.core.comm

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class JsonCodec implements Codec {

    @Override
    String encode(List<Command> commands) {
        def content = commands.collect { Command cmd ->
            def entry = cmd.properties
            ['class','metaClass'].each { entry.remove it }
            entry.className = cmd.class.name
            entry
        }
        JsonBuilder builder = new JsonBuilder(content)
        builder.toString()
    }

    @Override
    List<Command> decode(String transmitted) {
        def result = new LinkedList()
        def got = new JsonSlurper().parseText(transmitted)
        got.each { cmd ->
            Command responseCommand = Class.forName(cmd['className']).newInstance()
            cmd.each { key, value ->
                if (key == 'className') return
                if (key == 'id' && !(responseCommand instanceof NamedCommand)) return // set id only for NamedCommand
                if (key == 'attributeId') value = value.toLong()
                responseCommand[key] = value
            }
            result << responseCommand
        }
        return result
    }
}
