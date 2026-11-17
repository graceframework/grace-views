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
package org.grails.views.thymeleaf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * {@link EnableAutoConfiguration Auto-Configure} for Thymeleaf
 *
 * @author Michael Yan
 * @since 7.2.0
 */
@AutoConfiguration(after = ThymeleafAutoConfiguration.class)
@ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
@ConditionalOnBooleanProperty(name = "grails.views.thymeleaf.enabled", matchIfMissing = true)
public class ThymeleafViewAutoConfiguration {

    @Bean
    @ConditionalOnClass(SpringResourceTemplateResolver.class)
    public static SpringResourceTemplateResolverBeanPostProcessor springResourceTemplateResolverBeanPostProcessor() {
        return new SpringResourceTemplateResolverBeanPostProcessor();
    }

}
