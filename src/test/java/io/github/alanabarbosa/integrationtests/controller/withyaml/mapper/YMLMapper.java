package io.github.alanabarbosa.integrationtests.controller.withyaml.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.github.alanabarbosa.model.Category;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

public class YMLMapper implements ObjectMapper{
	
	private Logger logger = Logger.getLogger(YMLMapper.class.getName());
	
	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	protected TypeFactory typeFactory;

	public YMLMapper() {
		objectMapper = new com.fasterxml.jackson.databind.ObjectMapper(new YAMLFactory());
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);		
		
	    JavaTimeModule javaTimeModule = new JavaTimeModule();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
	    
	    objectMapper.registerModule(javaTimeModule);
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

	    typeFactory = TypeFactory.defaultInstance();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object deserialize(ObjectMapperDeserializationContext context) {
	    try {
	        String dataToDeserialize = context.getDataToDeserialize().asString();
	        Class type = (Class)context.getType();
	        
	        logger.info("Trying to deserialize object of type " + type);
	        
	        Object object = objectMapper.readValue(dataToDeserialize, typeFactory.constructType(type));
	        
	        if (object instanceof Category) {
	            Category category = (Category) object;
	            if (category.getName() == null) {
	                throw new RuntimeException("Field 'name' cannot be null");
	            }
	        }

	        return objectMapper.readValue(dataToDeserialize, typeFactory.constructType(type));
	    } catch (JsonMappingException e) {
	        logger.severe("Error mapping JSON: " + e.getMessage());
	        throw new RuntimeException("Error mapping JSON", e);
	    } catch (JsonProcessingException e) {
	        logger.severe("Error processing JSON: " + e.getMessage());
	        throw new RuntimeException("Error processing JSON", e);
	    }
	}

	@Override
	public Object serialize(ObjectMapperSerializationContext context) {
		try {
			return objectMapper.writeValueAsString(context.getObjectToSerialize());
		} catch (JsonProcessingException e) {
	        logger.severe("Serialization error: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Error serializing object", e);
			
			
		}
		//return null;
	}

}
