package com.cos.security1.config;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver();
        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8"); //내가 던지는 데이터는 html 파일이야
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html"); //.mustache말고 .html 파일을 찾게금 설정

        registry.viewResolver(resolver);

    }
}
