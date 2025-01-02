package io.github.alanabarbosa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi() {
    	return new OpenAPI()
    		    .info(new Info()
    		        .title("Blog RESTful API with Java 19 and Spring Boot 3")
    		        .version("v1")
    		        .description("This API provides RESTful web services for managing resources with Java 19 and Spring Boot 3. It includes features like JWT authentication, CRUD operations, and integration with MySQL.")
    		        .termsOfService("https://www.apache.org/licenses/LICENSE-2.0.html")
    		        .license(new License()
    		            .name("Apache 2.0")
    		            .url("https://www.apache.org/licenses/LICENSE-2.0.html")));

	}
}
