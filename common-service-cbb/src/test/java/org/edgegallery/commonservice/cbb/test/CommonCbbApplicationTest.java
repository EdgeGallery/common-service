package org.edgegallery.commonservice.cbb.test;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.servicecomb.springboot2.starter.EnableServiceComb;
import org.edgegallery.commonservice.cbb.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}, scanBasePackages = "org.edgegallery.developer")
@MapperScan(basePackages = {"org.edgegallery.commonservice.cbb"})
@EnableScheduling
@EnableServiceComb
public class CommonCbbApplicationTest {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(CommonCbbApplicationTest.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);
    }

    @Bean
    public OncePerRequestFilter accessTokenFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        };
    }
}
