package com.capstone.goat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket restAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.capstone.goat")) // 특정 패키지경로를 API문서화 한다.
                .paths(PathSelectors.any()) // apis중에서 특정 path조건 API만 문서화 하는 2차 필터
                .build()
                .groupName("API 1.0.0") // group별 명칭을 주어야 한다.
                .useDefaultResponseMessages(false); // 400,404,500 .. 표기를 ui에서 삭제한다.
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot REST API test")
                .version("v0.0.1")
                .description("스크랩 JPA swagger api 입니다.")
                .build();
    }
}
