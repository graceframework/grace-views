package grails.plugin.json.view

import grails.plugin.json.view.test.JsonViewTest
import spock.lang.Specification

class NullRenderingSpec extends Specification implements JsonViewTest {

    void "test rendering nulls with a domain"() {
        given:
        def templateText = '''
import grails.plugin.json.view.*

model {
    Player player
}

json g.render(player)
'''

        when:
        mappingContext.addPersistentEntity(Player)
        def renderResult = render(templateText, [player: new Player()])

        then:"No fields are rendered because they are null"
        renderResult.jsonText == '{}'
    }

    void "test rendering nulls with a domain (renderNulls = true)"() {
        given:
        def templateText = '''
import grails.plugin.json.view.*

model {
    Player player
}

json g.render(player, [renderNulls: true])
'''

        when:
        mappingContext.addPersistentEntity(Player)
        def renderResult = render(templateText, [player: new Player()])

        then:"No fields are rendered because they are null"
        renderResult.jsonText == '{"name":null,"team":null}'
    }

    void "test rendering nulls with a map"() {
        given:
        def templateText = '''
model {
    Map map
}

json g.render(map)
'''

        when:
        mappingContext.addPersistentEntity(Player)
        def renderResult = render(templateText, [map: [foo: null, bar: null]])

        then:"Maps with nulls are rendered by default"
        renderResult.jsonText == '{"foo":null,"bar":null}'
    }

    void "test rendering nulls with a pogo"() {
        given:
        def templateText = '''
model {
    Object obj
}

json g.render(obj)
'''

        when:
        mappingContext.addPersistentEntity(Player)
        def renderResult = render(templateText, [obj: new Child2()])

        then:"No fields are rendered because they are null"
        renderResult.jsonText == '{}'
    }

    void "test rendering nulls with a pogo (renderNulls = true)"() {
        given:
        def templateText = '''
model {
    Object obj
}

json g.render(obj, [renderNulls: true])
'''

        when:
        mappingContext.addPersistentEntity(Player)
        def renderResult = render(templateText, [obj: new Child2()])

        then:
        renderResult.jsonText == '{"name":null,"parent":null}'
    }
}
