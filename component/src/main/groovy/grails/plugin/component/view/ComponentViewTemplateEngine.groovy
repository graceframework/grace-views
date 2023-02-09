package grails.plugin.component.view

import grails.compiler.traits.TraitInjector
import grails.plugin.component.view.internal.ComponentViewsTransform
import grails.views.GrailsViewTemplate
import grails.views.ResolvableGroovyTemplateEngine
import grails.views.ViewCompilationException
import grails.views.WritableScript
import grails.views.WritableScriptTemplate
import grails.views.api.GrailsView
import grails.views.compiler.ViewsTransform
import groovy.text.Template
import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration
import groovy.text.markup.TemplateResolver
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.grails.core.io.support.GrailsFactoriesLoader

/**
 * A {@link ResolvableGroovyTemplateEngine} that uses Groovy's {@link MarkupTemplateEngine} internally
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@CompileStatic
class ComponentViewTemplateEngine extends ResolvableGroovyTemplateEngine {

    public static final String VIEW_BASE_CLASS = 'grails.views.component.baseClass'
    public static final String COMPILE_STATIC = 'grails.views.component.compileStatic'


    MarkupTemplateEngine innerEngine

    private final boolean compileStatic

    ComponentViewTemplateEngine(ComponentViewConfiguration config = new ComponentViewConfiguration(), ClassLoader classLoader = Thread.currentThread().contextClassLoader) {
        super(config, classLoader)
        this.compileStatic = compileStatic
        innerEngine = new MarkupTemplateEngine(classLoader, config, new TemplateResolver() {
            @Override
            void configure(ClassLoader templateClassLoader, TemplateConfiguration configuration) {
            }

            @Override
            URL resolveTemplate(String templatePath) throws IOException {
                return templateResolver.resolveTemplate(templatePath)
            }
        })
        prepareCustomizers(this.compilerConfiguration)
    }





    @Override
    WritableScriptTemplate createTemplate(String path, URL url) throws CompilationFailedException, ClassNotFoundException, IOException {
        prepareCustomizers(innerEngine.compilerConfiguration)
        def file = new File(url.file)

        try {
            def template = innerEngine.createTemplate(url)
            return createComponentViewTemplate(template)
        } catch (CompilationFailedException e) {
            throw new ViewCompilationException(e, file.canonicalPath)
        }

    }

    @Override
    WritableScriptTemplate createTemplate(File file) throws CompilationFailedException, ClassNotFoundException, IOException {
        prepareCustomizers(innerEngine.compilerConfiguration)
        try {
            def template = innerEngine.createTemplate(file.toURI().toURL())
            return createComponentViewTemplate(template)
        } catch (CompilationFailedException e) {
            throw new ViewCompilationException(e, file.canonicalPath)
        }

    }

    @Override
    WritableScriptTemplate createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        prepareCustomizers(innerEngine.compilerConfiguration)
        try {

            def template = innerEngine.createTemplate(reader)
            return createComponentViewTemplate(template)

        } catch (CompilationFailedException e) {
            throw new ViewCompilationException(e, "Generated")
        }

    }

    @CompileDynamic
    protected ComponentViewWritableScriptTemplate createComponentViewTemplate(Template template) {
        def clazz = template.@templateClass

        def componentViewTemplate = new ComponentViewWritableScriptTemplate(clazz, (File) null, innerEngine, viewConfiguration)
        super.initializeTemplate(componentViewTemplate, null)
        return componentViewTemplate
    }

    @Override
    String getDynamicTemplatePrefix() {
        "GeneratedComponentTemplate".intern()
    }

    @Override
    protected WritableScriptTemplate createTemplate(Class<? extends Template> cls, File sourceFile) {
        def template = new ComponentViewWritableScriptTemplate((Class<? extends GrailsView>) cls, sourceFile, innerEngine, (ComponentViewConfiguration) viewConfiguration)
        super.initializeTemplate(template, sourceFile)
    }

    @Override
    protected void prepareCustomizers(CompilerConfiguration cc) {
        if(innerEngine != null) {

            innerEngine.compilerConfiguration.compilationCustomizers.removeAll( this.compilerConfiguration.compilationCustomizers )
            CompilerConfiguration newConfig = new CompilerConfiguration(this.compilerConfiguration)
            super.prepareCustomizers(newConfig)

            if(compileStatic) {
                newConfig.addCompilationCustomizers(
                        new ASTTransformationCustomizer(Collections.singletonMap("extensions", "groovy.text.markup.MarkupTemplateTypeCheckingExtension"), CompileStatic.class));
            }

            innerEngine.compilerConfiguration.addCompilationCustomizers( newConfig.compilationCustomizers as CompilationCustomizer[])
        }

    }

    @Override
    protected ViewsTransform newViewsTransform() {
        return new ComponentViewsTransform(viewConfiguration.extension)
    }
}
