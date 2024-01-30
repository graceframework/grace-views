package grails.plugin.markup.view

import grails.boot.Grails
import grails.plugins.metadata.PluginSource
import groovy.transform.CompileStatic

@PluginSource
@CompileStatic
class Application {
    static void main(String[] args) {
        Grails.run(Application, args)
    }
}