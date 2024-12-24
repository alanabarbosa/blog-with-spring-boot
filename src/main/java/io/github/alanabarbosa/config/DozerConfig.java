package io.github.alanabarbosa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.model.Category;

@Configuration
public class DozerConfig {
    
    @Bean
    Mapper mapper() {
        return DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(CategoryVO.class, Category.class)
                            .fields("name", "name");
                    }
                })
                .build();
    }
}
