package grails.plugin.json.view

import grails.core.support.proxy.ProxyHandler
import grails.persistence.Entity
import grails.plugin.json.view.api.JsonView
import grails.plugin.json.view.api.internal.DefaultGrailsJsonViewHelper
import grails.plugin.json.view.test.JsonViewTest
import grails.views.GrailsViewTemplate
import grails.views.api.internal.EmptyParameters
import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

/*
 * Copyright 2014 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author graemerocher
 */
class JsonViewHelperSpec extends Specification implements JsonViewTest, GrailsUnitTest {

    void "test render toMany association"() {
        given:"A view helper"
        DefaultGrailsJsonViewHelper viewHelper = mockViewHelper(Team, Player)
        def player1 = new Player(name: "Iniesta")
        def player2 = new Player(name: "Messi")
        def team = new Team(name:"Barcelona", players: [player1, player2])

        when:"We render an object without deep argument and no child id"

        def result = viewHelper.render(team)

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","players":[{"name":"Iniesta"},{"name":"Messi"}]}'

        when:"We render an object without deep argument and child ids"

        player1.id = 1L
        player2.id = 2L
        result = viewHelper.render(team)

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","players":[{"id":1},{"id":2}]}'

        when:"We render an object with deep argument and child ids"

        player1.id = 1L
        player2.id = 2L
        result = viewHelper.render(team, [deep: true])

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","players":[{"id":1,"name":"Iniesta"},{"id":2,"name":"Messi"}]}'
    }


    void "test jsonapi render toMany association"() {
        given:"A view helper"
        mappingContext.addPersistentEntities(Player, Team)
        def player1 = new Player(name: "Iniesta")
        player1.id = 1
        def player2 = new Player(name: "Messi")
        player2.id = 2
        def team = new Team(name:"Barcelona", players: [player1, player2])
        team.id = 1

        when:"We render an object without deep argument and no child id"
        def renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team)
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona","titles":null},"relationships":{"captain":{"data":null},"players":{"data":[{"type":"player","id":"1"},{"type":"player","id":"2"}]}}},"links":{"self":"/team/1"}}'
    }

    void "test render toOne association"() {
        given:"A view helper"
        DefaultGrailsJsonViewHelper viewHelper = mockViewHelper(Team, Player)

        when:"We render an object without deep argument and no child id"

        def player = new Player(name: "Iniesta")
        def team = new Team(name:"Barcelona",titles: ['La Liga'], captain: player)
        def result = viewHelper.render(team)

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","titles":["La Liga"]}'

        when:"We render an object with deep argument and no child id"

        result = viewHelper.render(team, [deep:true])

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","captain":{"name":"Iniesta"},"titles":["La Liga"]}'

        when:"We render an object without deep argument and a child id"

        player.id = 1L
        result = viewHelper.render(team)

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","captain":{"id":1},"titles":["La Liga"]}'

        when:"We render an object with deep argument and a child id"

        player.id = 1L
        result = viewHelper.render(team, [deep:true])

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","captain":{"id":1,"name":"Iniesta"},"titles":["La Liga"]}'

        when:"We render an object with deep argument and a child id and excludes"

        player.id = 1L
        result = viewHelper.render(team, [deep:true, excludes: ['captain.name']])

        then:"The result is correct"
        result.toString() == '{"name":"Barcelona","captain":{"id":1},"titles":["La Liga"]}'

        when:"We render an object with deep argument and a child id and includes"

        player.id = 1L
        result = viewHelper.render(team, [deep:true, includes: ['captain.name']])

        then:"The result is correct"
        result.toString() == '{"captain":{"name":"Iniesta"}}'
    }

    void "test includes with json api"() {
        given:"A view helper"
        mappingContext.addPersistentEntities(Player, Team)
        def player1 = new Player(name: "Iniesta")
        player1.id = 1
        def player2 = new Player(name: "Messi")
        player2.id = 2
        def team = new Team(name:"Barcelona", players: [player1, player2])
        team.id = 1

        when:"We render an object without deep argument and no child id"
        def renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [includes: ['name']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona"},"relationships":{}},"links":{"self":"/team/1"}}'

        when:"We render an object without deep argument and no child id"
        renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [includes: ['name', 'captain']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona"},"relationships":{"captain":{"data":null}}},"links":{"self":"/team/1"}}'
    }

    void "test excludes with json api"() {
        given:"A view helper"
        mappingContext.addPersistentEntities(Player, Team)
        def player1 = new Player(name: "Iniesta")
        player1.id = 1
        def player2 = new Player(name: "Messi")
        player2.id = 2
        def team = new Team(name:"Barcelona", players: [player1, player2])
        team.id = 1

        when:"We render an object with multiple excludes"
        def renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [excludes: ['captain', 'players', 'titles']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona"},"relationships":{}},"links":{"self":"/team/1"}}'

        when:"We render an object with a single excludes"
        renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [excludes: ['name']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"titles":null},"relationships":{"captain":{"data":null},"players":{"data":[{"type":"player","id":"1"},{"type":"player","id":"2"}]}}},"links":{"self":"/team/1"}}'

        when:"We expand a relationship"
        renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [expand: ['players']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona","titles":null},"relationships":{"captain":{"data":null},"players":{"data":[{"type":"player","id":"1"},{"type":"player","id":"2"}]}}},"links":{"self":"/team/1"},"included":[{"type":"player","id":"1","attributes":{"name":"Iniesta"},"relationships":{"team":{"data":null}},"links":{"self":"/player/1"}},{"type":"player","id":"2","attributes":{"name":"Messi"},"relationships":{"team":{"data":null}},"links":{"self":"/player/2"}}]}'

        when:"We expand a relationship and exclude a nested property"
        team.captain = new Player(name: "Captain Hook")
        team.captain.id = 10
        renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [expand: ['captain'], excludes: ['captain.name']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona","titles":null},"relationships":{"captain":{"links":{"self":"/player/10"},"data":{"type":"player","id":"10"}},"players":{"data":[{"type":"player","id":"1"},{"type":"player","id":"2"}]}}},"links":{"self":"/team/1"},"included":[{"type":"player","id":"10","attributes":{},"relationships":{"team":{"data":null}},"links":{"self":"/player/10"}}]}'

        when:"We expand a relationship and exclude a nested property"
        team.captain = new Player(name: "Captain Hook")
        team.captain.id = 10
        renderResult = render('''
        import groovy.transform.*
        import grails.plugin.json.view.*

        @Field Team team

        json jsonapi.render(team, [expand: ['players'], excludes: ['players.name']])
        ''', [team:team])

        then:"The result is correct"
        renderResult.jsonText == '{"data":{"type":"team","id":"1","attributes":{"name":"Barcelona","titles":null},"relationships":{"captain":{"links":{"self":"/player/10"},"data":{"type":"player","id":"10"}},"players":{"data":[{"type":"player","id":"1"},{"type":"player","id":"2"}]}}},"links":{"self":"/team/1"},"included":[{"type":"player","id":"1","attributes":{},"relationships":{"team":{"data":null}},"links":{"self":"/player/1"}},{"type":"player","id":"2","attributes":{},"relationships":{"team":{"data":null}},"links":{"self":"/player/2"}}]}'
    }

    void "Test render object method with customizer"() {
        given:"A view helper"
        DefaultGrailsJsonViewHelper viewHelper = mockViewHelper(Test)

        when:"We render an object"

        def test = new Test(title: "The Stand", author: new TestAuthor(name: "Stephen King"))
        test.id = 1L
        def result = viewHelper.render(test) {
            pages 1000
        }
        then:"The result is correct"
        result.toString() == '{"id":1,"title":"The Stand","author":{"name":"Stephen King"},"pages":1000}'
    }

    void "Test render object method with customizer when not configured as a domain"() {
        given:"A view helper"
        DefaultGrailsJsonViewHelper viewHelper = mockViewHelper()

        when:"We render an object"

        def test = new Test(title: "The Stand", author: new TestAuthor(name: "Stephen King"))
        test.id = 1L
        def result = viewHelper.render(test) {
            pages 1000
        }
        then:"The result is correct"
        result.toString() == '{"author":{"name":"Stephen King"},"id":1,"title":"The Stand","pages":1000}'
    }

    void "Test render object method"() {
        given:"A view helper"
        def viewHelper = mockViewHelper(Test)

        when:"We render an object"

        def test = new Test(title: "The Stand", author: new TestAuthor(name: "Stephen King"))
        test.id = 1L
        def result = viewHelper.render(test)
        then:"The result is correct"
        result.toString() == '{"id":1,"title":"The Stand","author":{"name":"Stephen King"}}'

        when:"We render an object"
        result = viewHelper.render(new Test(title:"The Stand", author:new TestAuthor(name:"Stephen King")), [excludes:['author']])
        then:"The result is correct"
        result.toString() == '{"title":"The Stand"}'
    }

    void "Test render object method with plain object"() {
        given:"A view helper"
        def viewHelper = mockViewHelper(Test)

        when:"We render an object"
        def result = viewHelper.render(new Test2(title:"The Stand", author:"Stephen King"))
        then:"The result is correct"
        result.toString() == '{"author":"Stephen King","title":"The Stand"}'

        when:"We render an object"
        result = viewHelper.render(new Test2(title:"The Stand", author:"Stephen King"), [excludes:['author']])
        then:"The result is correct"
        result.toString() == '{"title":"The Stand"}'
    }

    void "Test render collection as null produces empty list"() {
        given:"A view helper"
        def viewHelper = mockViewHelper()

        when:
        def result = viewHelper.render(collection: null, template: 'child', var: 'age')

        then:"The result is correct"
        result.toString() == '[]'

        when:
        result = viewHelper.render(collection: [], template: 'child', var: 'age')

        then:"The result is correct"
        result.toString() == '[]'
    }

    protected DefaultGrailsJsonViewHelper mockViewHelper(Class...classes) {
        def jsonView = Mock(JsonView)
        jsonView.getParams() >> new EmptyParameters()

        KeyValueMappingContext mappingContext = new KeyValueMappingContext("test")
        mappingContext.addPersistentEntities(classes)

        jsonView.getMappingContext() >> mappingContext

        def viewHelper = new DefaultGrailsJsonViewHelper(jsonView)

        def binding = new Binding()
        jsonView.getBinding() >> binding

        jsonView.getTemplateEngine() >> templateEngine

        jsonView.getViewTemplate() >> new GrailsViewTemplate(JsonView)

        jsonView.getProxyHandler() >> Mock(ProxyHandler) {
            isProxy(_) >> false
        }

        viewHelper
    }
}

@Entity
class Team {
    String name
    Player captain
    List players
    List<String> titles
    static hasMany = [players:Player]
}
@Entity
class Player {
    Long version
    String name
    static belongsTo = [team:Team]
}

@Entity
class PlayerWithAge {
    String name
    int age
}

@Entity
class Circular {
    String name
}

@Entity
class Test {
    String title
    TestAuthor author

    static embedded = ['author']
}

class TestAuthor {
    String name
}

class Test2 {
    String title
    String author
}
