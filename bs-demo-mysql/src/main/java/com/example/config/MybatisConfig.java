package com.example.config;

import com.example.config.mybatis.PerfInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public PerfInterceptor getPerfInterceptor() {
        return new PerfInterceptor();
    }

}
