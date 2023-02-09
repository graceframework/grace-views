package grails.views.gradle.component

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input

import grails.views.gradle.AbstractGroovyTemplateCompileTask

/**
 * MarkupView compiler task for Gradle
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@CompileStatic
class ComponentViewCompilerTask extends AbstractGroovyTemplateCompileTask {

    @Input
    @Override
    String getFileExtension() {
        "gcom"
    }

    @Input
    @Override
    String getScriptBaseName() {
        "grails.plugin.component.view.ComponentViewTemplate"
    }

    @Input
    @Override
    protected String getCompilerName() {
        "grails.plugin.component.view.ComponentViewCompiler"
    }

}
