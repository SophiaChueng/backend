package com.yizhi.training.application.v2.config;

import com.yizhi.core.application.config.YizhiWebMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.any;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends YizhiWebMvcConfigurer {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).groupName("项目").genericModelSubstitutes(DeferredResult.class)
            .genericModelSubstitutes(ResponseEntity.class).useDefaultResponseMessages(false).forCodeGeneration(true)
            .pathMapping("/").apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.yizhi"))
            .paths(PathSelectors.regex("^(?!.*?/remote).*$"))
            .paths(any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("项目服务").version("1.0").build();
    }
}
