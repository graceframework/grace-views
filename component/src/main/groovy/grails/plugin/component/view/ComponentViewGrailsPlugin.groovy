package grails.plugin.component.view

import grails.plugin.component.view.mvc.ComponentViewResolver
import grails.plugins.Plugin
import grails.util.BuildSettings
import grails.util.Environment
import grails.util.Metadata
import grails.views.ViewsEnvironment
import grails.views.mvc.GenericGroovyTemplateViewResolver
import grails.views.resolve.PluginAwareTemplateResolver
import org.grails.io.support.GrailsResourceUtils

/**
 * Plugin class for markup views
 *
 * @author Graeme Rocher
 * @since 1.0
 */
class ComponentViewGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2022.0.0 > *"

    def title = "Component View" // Headline display name of the plugin
    def author = "Graeme Rocher"
    def authorEmail = "graeme.rocher@gmail.com"
    def description = '''\
A plugin that allows rendering of Component views
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.github.io/grails-views/latest/"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Grace Framework", url: "https://graceframework.org/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Michael Yan", email: "rain@rainboyan.com" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "Github", url: "http://github.com/graceframework/grace-views/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "http://github.com/graceframework/grace-views" ]

    Closure doWithSpring() { {->
        componentViewConfiguration(ComponentViewConfiguration)
        componentTemplateEngine(ComponentViewTemplateEngine, componentViewConfiguration, applicationContext.classLoader)
        smartComponentViewResolver(ComponentViewResolver, componentTemplateEngine) {
            templateResolver = bean(PluginAwareTemplateResolver, componentViewConfiguration)
        }
        componentViewResolver(GenericGroovyTemplateViewResolver, smartComponentViewResolver)
    } }
}
