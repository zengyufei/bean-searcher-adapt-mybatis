package com.example.config;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.SqlExecutor;
import cn.zhxu.bs.SqlResolver;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.group.GroupPair;
import com.example.config.bs.MyMySqlDialect;
import com.example.config.bs.MySqlExecutor;
import com.example.config.bs.MySqlResolver;
import com.example.config.bs.op.MyBetween;
import com.example.config.bs.op.MyEndWith;
import com.example.config.bs.op.MyEqual;
import com.example.config.bs.op.MyGreaterEqual;
import com.example.config.bs.op.MyGreaterThan;
import com.example.config.bs.op.MyInList;
import com.example.config.bs.op.MyLessEqual;
import com.example.config.bs.op.MyLessThan;
import com.example.config.bs.op.MyLike;
import com.example.config.bs.op.MyNotBetween;
import com.example.config.bs.op.MyNotEqual;
import com.example.config.bs.op.MyNotIn;
import com.example.config.bs.op.MyNotLike;
import com.example.config.bs.op.MyOrLike;
import com.example.config.bs.op.MyStartWith;
import com.example.config.mybatis.PerfInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MybatisConfig {

    @Bean
    public PerfInterceptor getPerfInterceptor() {
        return new PerfInterceptor();
    }

}
