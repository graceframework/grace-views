/*
 * Copyright 2024-2026 the original author or authors.
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
package org.grails.views.groovy;

import java.io.File;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.groovy.template.autoconfigure.GroovyTemplateProperties;

import grails.util.BuildSettings;
import grails.util.Environment;

/**
 * {@link BeanPostProcessor} to configure {@link GroovyTemplateProperties}
 *
 * @author Michael Yan
 * @since 7.2.0
 */
public class GroovyTemplatePropertiesBeanPostProcessor implements BeanPostProcessor {

    public static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:/views/";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof GroovyTemplateProperties properties) {
            if (Environment.isDevelopmentMode()) {
                properties.setCache(false);
                properties.setResourceLoaderPath(new File(BuildSettings.GRAILS_APP_DIR, "views").toURI().toString());
            }
            else {
                properties.setResourceLoaderPath(DEFAULT_RESOURCE_LOADER_PATH);
            }
        }
        return bean;
    }

}
