package com.example.config.mybatis;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;


/**
 * Mybatis拦截器，监控慢sql
 *
 * @author zengyufei
 * @date 2023/03/18
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        ),
        @Signature(
                type = StatementHandler.class,
                method = "update",
                args = {Statement.class}
        ),
        @Signature(
                type = StatementHandler.class,
                method = "batch",
                args = {Statement.class}
        )
})

public class PerfInterceptor implements Interceptor {
    static final Logger log = LoggerFactory.getLogger(PerfInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        long start = System.currentTimeMillis();
        Object res = invocation.proceed();
        long latency = System.currentTimeMillis() - start;

        // type：sql类型：如update, query
        String type = mappedStatement.getSqlCommandType().name();
        log.info("我是mybatis拦截器, 拦截到:  类型={} 耗时={}ms sql = {}", type, latency, boundSql.getSql());
        return res;
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

}
