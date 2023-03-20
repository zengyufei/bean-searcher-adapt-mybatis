package com.example.config;

import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.dialect.Dialect;
import com.xunmo.bs.config.bs.MyPostgreSqlDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanSearchConfig {
    @Bean
    public Dialect getPostgreSqlDialect(BeanSearcherProperties config) {
        final BeanSearcherProperties.Params params = config.getParams();
        final BeanSearcherProperties.Params.Pagination pagination = params.getPagination();
        return new MyPostgreSqlDialect(pagination.getPage(), pagination.getSize());
    }

}
