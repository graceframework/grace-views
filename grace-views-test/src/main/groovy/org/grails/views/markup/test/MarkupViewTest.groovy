/*
 * Copyright 2018-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.views.markup.test

import groovy.json.JsonSlurper
import groovy.text.Template
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.StaticMessageSource
import org.springframework.http.HttpStatus

import grails.core.GrailsApplication
import grails.views.api.HttpView
import grails.views.api.http.Response
import grails.web.mapping.LinkGenerator
import grails.web.mime.MimeUtility

import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.views.markup.MarkupViewConfiguration
import org.grails.views.markup.MarkupViewTemplateEngine
import org.grails.views.markup.api.MarkupView


/**
 * A trait that test classes can implement to add support for easily testing JSON views
 *
 * @author Graeme Rocher
 * @since 7.2.0
 */
@CompileStatic
trait MarkupViewTest {

    @Autowired(required = false)
    MessageSource messageSource = new StaticMessageSource()

    @Autowired(required = false)
    MappingContext mappingContext = {
        def ctx = new KeyValueMappingContext('test')
        ctx.setCanInitializeEntities(true)
        return ctx
    }()

    @Autowired(required = false)
    MimeUtility mimeUtility

    @Autowired(required = false)
    LinkGenerator linkGenerator = new TestLinkGenerator(mappingContext)

    @Autowired(required = false)
    MarkupViewConfiguration viewConfiguration = new MarkupViewConfiguration()

    @Autowired(required = false)
    void setGrailsApplication(GrailsApplication grailsApplication) {
        viewConfiguration.setGrailsApplication(grailsApplication)
    }

    @Lazy
    MarkupViewTemplateEngine templateEngine = {
        def templateEngine = new MarkupViewTemplateEngine(viewConfiguration, Thread.currentThread().contextClassLoader)
        if (messageSource != null) {
            templateEngine.setMessageSource(messageSource)
        }
        templateEngine.setLinkGenerator(linkGenerator)
        if (mimeUtility != null) {
            templateEngine.setMimeUtility(mimeUtility)
        }
        if (mappingContext != null) {
            templateEngine.setMappingContext(mappingContext)
        }
        return templateEngine
    }()

    /**
     * Render a template for the given source
     *
     * @param source The raw source of the template
     * @param model The model
     *
     * @return The result
     */
    MarkupRenderResult render(String source) {
        render(source, Collections.emptyMap(), null)
    }

    /**
     * Render a template for the given source
     *
     * @param source The raw source of the template
     * @param model The model
     *
     * @return The result
     */
    MarkupRenderResult render(String source, Map model) {
        render(source, model, null)
    }

    /**
     * Render a template for the given source
     *
     * @param source The raw source of the template
     * @param model The model
     * @param configurer The configurer
     *
     * @return The result
     */
    MarkupRenderResult render(String source, Map model, @DelegatesTo(org.grails.views.markup.test.TestRequestConfigurer) Closure configurer) {
        def template = templateEngine.createTemplate(source)
        return produceResult(template, model, configurer)
    }

    /**
     * Render a template for the given source
     *
     * @param source The raw source of the template
     * @param model The model
     * @param configurer The configurer
     *
     * @return The result
     */
    MarkupRenderResult render(String source, @DelegatesTo(org.grails.views.markup.test.TestRequestConfigurer) Closure configurer) {
        def template = templateEngine.createTemplate(source)
        return produceResult(template, Collections.emptyMap(), configurer)
    }

    /**
     * Render one of the GSON views in the grails-app/views directory for the given arguments
     *
     * @param arguments The named arguments: 'template', 'view' and 'model'
     *
     * @return The render result
     */
    MarkupRenderResult render(Map arguments) {
        render arguments, null
    }

    /**
     * Render one of the GSON views in the grails-app/views directory for the given arguments
     *
     * @param arguments The named arguments: 'template', 'view' and 'model'
     * @param configurer The configurer for modifying the request state of the view
     *
     * @return The render result
     */
    MarkupRenderResult render(Map arguments, @DelegatesTo(org.grails.views.markup.test.TestRequestConfigurer) Closure configurer) {
        String viewUri
        if (arguments.template) {
            viewUri = templateEngine.viewUriResolver.resolveTemplateUri(null, arguments.template.toString())
        } else if (arguments.view) {
            viewUri = arguments.view.toString()
        } else {
            throw new IllegalArgumentException("Either a 'view' or 'template' argument is required!")
        }
        def template = templateEngine.resolveTemplate(viewUri)

        if (template == null) {
            throw new IllegalArgumentException("No view or template found for URI $viewUri")
        }

        def model = arguments.model instanceof Map ? (Map) arguments.model : [:]
        return produceResult(template, model, configurer)
    }

    private MarkupRenderResult produceResult(Template template, Map model, Closure configurer) {
        MarkupView writable = (MarkupView) template.make(model)
        if (configurer != null) {
            def rc = new org.grails.views.markup.test.TestRequestConfigurer(writable)
            configurer.delegate = rc
            configurer.call()
        }

        def result = new MarkupRenderResult()
        if (writable instanceof HttpView) {
            def httpView = (HttpView) writable
            httpView.setResponse(new TestHttpResponse(result))
        }

        def sw = new StringWriter()
        writable.writeTo(sw)
        def str = sw.toString()
        result.jsonText = str
        result.json = new JsonSlurper().parseText(str)
        return result
    }

    static class TestHttpResponse implements Response {

        final MarkupRenderResult result

        TestHttpResponse(MarkupRenderResult result) {
            this.result = result
        }

        @Override
        void header(String name, String value) {
            result.headers[name] = value
        }

        @Override
        void header(Map<String, String> nameAndValue) {
            result.headers.putAll(nameAndValue)
        }

        @Override
        void headers(Map<String, String> namesAndValues) {
            result.headers.putAll(namesAndValues)
        }

        @Override
        void contentType(String contentType) {
            result.contentType = contentType
        }

        @Override
        void encoding(String encoding) {
            // ignore
        }

        @Override
        void status(int status) {
            result.status = HttpStatus.valueOf(status)
        }

        @Override
        void status(int status, String message) {
            result.status = HttpStatus.valueOf(status)
            result.message = message
        }

        @Override
        void status(HttpStatus status) {
            result.status = status
            result.message = status.getReasonPhrase()
        }

        @Override
        void status(HttpStatus status, String message) {
            result.status = status
            result.message = message
        }

    }

}
