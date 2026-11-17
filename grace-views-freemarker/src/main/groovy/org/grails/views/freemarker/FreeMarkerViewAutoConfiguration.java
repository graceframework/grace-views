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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.freemarker.autoconfigure.FreeMarkerAutoConfiguration;
import org.springframework.boot.freemarker.autoconfigure.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

/**
 * {@link EnableAutoConfiguration Auto-Configure} for FreeMarker.
 *
 * @author Michael Yan
 * @since 7.2.0
 */
@AutoConfiguration(after = { FreeMarkerAutoConfiguration.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnBooleanProperty(name = "grails.views.freemarker.enabled", matchIfMissing = true)
@ConditionalOnClass({ freemarker.template.Configuration.class, FreeMarkerConfigurationFactory.class })
public class FreeMarkerViewAutoConfiguration {

    @Bean
    @ConditionalOnBean(FreeMarkerProperties.class)
    public static FreeMarkerPropertiesBeanPostProcessor freeMarkerPropertiesBeanPostProcessor() {
        return new FreeMarkerPropertiesBeanPostProcessor();
    }

}
