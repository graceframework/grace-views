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
package org.grails.views.freemarker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;

import grails.util.BuildSettings;
import grails.util.Environment;

/**
 * {@link BeanPostProcessor} to configure {@link FreeMarkerProperties}
 *
 * @author Michael Yan
 * @since 7.2.0
 */
public class FreeMarkerPropertiesBeanPostProcessor implements BeanPostProcessor {

    public static final String DEFAULT_TEMPLATE_LOADER_PATH = "classpath:/views/";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FreeMarkerProperties properties) {
            List<String> templateLoaderPaths = new ArrayList<>(List.of(properties.getTemplateLoaderPath()));
            if (Environment.isDevelopmentMode()) {
                templateLoaderPaths.add(new File(BuildSettings.GRAILS_APP_DIR, "views").toURI().toString());
            }
            else {
                templateLoaderPaths.add(DEFAULT_TEMPLATE_LOADER_PATH);
            }
            properties.setTemplateLoaderPath(templateLoaderPaths.toArray(new String[0]));
        }
        return bean;
    }

}
