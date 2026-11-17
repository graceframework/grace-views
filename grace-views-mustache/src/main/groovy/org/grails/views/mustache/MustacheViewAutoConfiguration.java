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
package org.grails.views.mustache;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.mustache.autoconfigure.MustacheAutoConfiguration;
import org.springframework.boot.mustache.autoconfigure.MustacheProperties;
import org.springframework.boot.mustache.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-Configure} for Mustache.
 *
 * @author Michael Yan
 * @since 7.2.0
 */
@AutoConfiguration(after = { MustacheAutoConfiguration.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnBooleanProperty(name = "grails.views.mustache.enabled", matchIfMissing = true)
@ConditionalOnClass({ Mustache.class, MustacheViewResolver.class })
public class MustacheViewAutoConfiguration {

    @Bean
    @ConditionalOnClass(MustacheProperties.class)
    public static MustachePropertiesBeanPostProcessor mustachePropertiesBeanPostProcessor() {
        return new MustachePropertiesBeanPostProcessor();
    }

    @Bean
    @ConditionalOnBean(value = MustacheViewResolver.class)
    public static MustacheViewResolverPostProcessor mustacheViewResolverPostProcessor() {
        return new MustacheViewResolverPostProcessor();
    }

}
