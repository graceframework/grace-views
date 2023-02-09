package grails.plugin.component.view

import grails.plugin.component.view.internal.ComponentViewsTransform
import grails.views.AbstractGroovyTemplateCompiler
import grails.views.compiler.ViewsTransform
import groovy.io.FileType
import groovy.text.markup.BaseTemplate
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.CompilationCustomizer
/**
 * A compiler for markup templates
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@InheritConstructors
@CompileStatic
class ComponentViewCompiler extends AbstractGroovyTemplateCompiler {

    @Override
    protected CompilerConfiguration configureCompiler(CompilerConfiguration configuration) {
        super.configureCompiler(configuration)
        def templateCustomizer = (CompilationCustomizer) getClass().classLoader.loadClass("groovy.text.markup.TemplateASTTransformer")
                .newInstance(viewConfiguration)
        configuration.addCompilationCustomizers(templateCustomizer)
        if(viewConfiguration.compileStatic) {
            configuration.addCompilationCustomizers(
                    new ASTTransformationCustomizer(Collections.singletonMap("extensions", "groovy.text.markup.MarkupTemplateTypeCheckingExtension"), CompileStatic.class));
        }
        return configuration
    }

    @Override
    protected ViewsTransform newViewsTransform() {
        return new ComponentViewsTransform(viewConfiguration.extension)
    }

    static void main(String[] args) {
        run(args, ComponentViewConfiguration, ComponentViewCompiler)
    }
}

