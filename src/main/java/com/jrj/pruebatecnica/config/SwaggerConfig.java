
package com.jrj.pruebatecnica.config;



import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket apiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jrj.pruebatecnica.Controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }
    public ApiInfo getApiInfo(){
        return new ApiInfo("Ecommerce prueba técnica Hiberus", 
                "Ecommerce Spring Boot JPA REST ", "1.0", 
                "http://localhost:8080/licence", 
                new Contact("JRJ ORG", "http://jrj.es", "juandelarubia@msn.com")
                , "LICENCE ", "http://localhost:8080/licence", Collections.emptyList());
    }
}
