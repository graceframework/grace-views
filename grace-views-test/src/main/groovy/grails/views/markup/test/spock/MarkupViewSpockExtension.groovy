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
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

import grails.testing.views.json.JsonViewUnitTest

@CompileStatic
class MarkupViewSpockExtension implements IGlobalExtension {

    MarkupViewSetupSpecInterceptor markupViewSetupSpecInterceptor = new MarkupViewSetupSpecInterceptor()

    @Override
    void visitSpec(SpecInfo spec) {
        if (JsonViewUnitTest.isAssignableFrom(spec.reflection)) {
            spec.addSetupSpecInterceptor(markupViewSetupSpecInterceptor)
        }
    }
}
