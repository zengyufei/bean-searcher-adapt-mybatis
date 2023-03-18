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
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanSearchConfig {


//    @Bean
//    public Dialect getPostgreSqlDialect(BeanSearcherProperties config) {
//        final BeanSearcherProperties.Params params = config.getParams();
//        final BeanSearcherProperties.Params.Pagination pagination = params.getPagination();
//        return new MyPostgreSqlDialect(pagination.getPage(), pagination.getSize());
//    }

    @Bean
    public Dialect getMysqlSqlDialect(BeanSearcherProperties config) {
        final BeanSearcherProperties.Params params = config.getParams();
        final BeanSearcherProperties.Params.Pagination pagination = params.getPagination();
        return new MyMySqlDialect(pagination.getPage(), pagination.getSize());
    }

    @Bean
    @Primary
    public SqlExecutor regMyDefaultSqlExecutor(SqlSessionFactory sqlSessionFactory) {
        return new MySqlExecutor(sqlSessionFactory);
    }

    @Bean
    public FieldOpPool myFieldOpPool() {
        List<FieldOp> ops = new ArrayList<>();
        // 添加自己喜欢的字段运算符全部 add 进去即可
        // 这里没添加的运算符将不可用
        ops.add(new MyLike());
        ops.add(new MyNotLike());
        ops.add(new MyOrLike());

        ops.add(new MyEqual());
        ops.add(new MyNotEqual());

        ops.add(new MyBetween());
        ops.add(new MyNotBetween());

        ops.add(new MyStartWith());
        ops.add(new MyEndWith());

        ops.add(new MyGreaterEqual());
        ops.add(new MyGreaterThan());

        ops.add(new MyLessEqual());
        ops.add(new MyLessThan());

        ops.add(new MyInList());
        ops.add(new MyNotIn());

        return new FieldOpPool(ops);
    }

    @Bean
    public SqlResolver sqlResolver(Dialect dialect, GroupPair.Resolver groupPairResolver, BeanSearcherProperties config) {
        MySqlResolver resolver = new MySqlResolver(dialect);
        resolver.setConfig(config);
        resolver.setGroupPairResolver(groupPairResolver);
        return resolver;
    }
}
