package io.github.alanabarbosa.serialization.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlJackson2HttpMessageConverter() {
        super(createYamlMapper(), MediaType.parseMediaType("application/x-yaml"));
    }

    private static ObjectMapper createYamlMapper() {
        YAMLMapper yamlMapper = new YAMLMapper();
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

        yamlMapper.registerModule(javaTimeModule);
        return yamlMapper;
    }
}
