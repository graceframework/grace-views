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
package grails.views.markup.test.spock

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.springframework.web.servlet.i18n.SessionLocaleResolver

import grails.core.GrailsApplication
import grails.views.markup.test.MarkupViewUnitTest
import grails.views.mvc.GenericGroovyTemplateViewResolver
import grails.views.resolve.PluginAwareTemplateResolver

import org.grails.datastore.mapping.keyvalue.mapping.config.KeyValueMappingContext
import org.grails.views.markup.MarkupViewConfiguration
import org.grails.views.markup.MarkupViewTemplateEngine
import org.grails.views.markup.mvc.MarkupViewResolver
import org.grails.web.mapping.DefaultLinkGenerator
import org.grails.web.mapping.UrlMappingsHolderFactoryBean

@CompileStatic
class MarkupViewSetupSpecInterceptor implements IMethodInterceptor {

    @Override
    void intercept(IMethodInvocation invocation) throws Throwable {
        MarkupViewUnitTest test = (MarkupViewUnitTest)invocation.instance
        setup(test)
        invocation.proceed()
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected void setup(MarkupViewUnitTest test) {

        GrailsApplication grailsApplication = test.grailsApplication
        def config = grailsApplication.config

        test.defineBeans {
            grailsLinkGenerator(DefaultLinkGenerator, config?.grails?.serverURL ?: "http://localhost:8080")
            localeResolver(SessionLocaleResolver)
            grailsUrlMappingsHolder(UrlMappingsHolderFactoryBean) {
                grailsApplication = grailsApplication
            }
            grailsDomainClassMappingContext(KeyValueMappingContext, 'test') {
                canInitializeEntities = true
            }
            markupViewConfiguration(MarkupViewConfiguration)
            markupTemplateEngine(MarkupViewTemplateEngine, markupViewConfiguration, grailsApplication.classLoader)
            markupSmartViewResolver(MarkupViewResolver, markupTemplateEngine) {
                templateResolver = bean(PluginAwareTemplateResolver, markupViewConfiguration)
            }
            markupViewResolver(GenericGroovyTemplateViewResolver, markupSmartViewResolver)
        }

    }
}
