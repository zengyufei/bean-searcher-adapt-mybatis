package com.xunmo.bs.config.bs.op;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.ObjectUtils;
import com.xunmo.bs.config.bs.FieldOpParam;
import com.xunmo.bs.config.bs.MyPostgreSqlDialect;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

public class MyContain extends Contain implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        final String name = getFieldParam().getName();
        final Dialect dialect = this.getDialect();
//        final DbType dbType = fieldMeta.getDbType();
//        final Class<?> type = dbType.getType();
        if (opPara.isIgnoreCase()) {
            if (hasILike()) {
                sqlBuilder.append(fieldSql.getSql());
                if (dialect instanceof MyPostgreSqlDialect) {
                    // pgsql
                    sqlBuilder.append(" ilike '%' || #{").append(name).append("}");
                    // 使用 postgresql 42.3.8 以上，则不需要下面注释
//                    if (Number.class.isAssignableFrom(type)) {
//                        sqlBuilder.append("::int");
//                    } else if (String.class.isAssignableFrom(type)) {
//                        sqlBuilder.append("::text");
//                    }
                    sqlBuilder.append(" || '%'");
                } else {
                    // mysql
                    sqlBuilder.append(" ilike").append(" CONCAT('%',#{").append(name).append("},'%')");
                }
            } else {
                toUpperCase(sqlBuilder, fieldSql.getSql());
                if (dialect instanceof MyPostgreSqlDialect) {
                    // pgsql
                    sqlBuilder.append(" like '%' || #{").append(name).append("}");
                    // 使用 postgresql 42.3.8 以上，则不需要下面注释
//                    if (Number.class.isAssignableFrom(type)) {
//                        sqlBuilder.append("::int");
//                    } else if (String.class.isAssignableFrom(type)) {
//                        sqlBuilder.append("::text");
//                    }
                    sqlBuilder.append(" || '%'");
                } else {
                    // mysql
                    sqlBuilder.append(" like").append(" CONCAT('%',#{").append(name).append("},'%')");
                }
                ObjectUtils.upperCase(values);
            }
        } else {
            sqlBuilder.append(fieldSql.getSql());
            if (dialect instanceof MyPostgreSqlDialect) {
                // pgsql
                sqlBuilder.append(" like '%' || #{").append(name).append("}");
                // 使用 postgresql 42.3.8 以上，则不需要下面注释
//                if (Number.class.isAssignableFrom(type)) {
//                    sqlBuilder.append("::int");
//                } else if (String.class.isAssignableFrom(type)) {
//                    sqlBuilder.append("::text");
//                }
                sqlBuilder.append(" || '%'");
            } else {
                // mysql
                sqlBuilder.append(" like").append(" CONCAT('%',#{").append(name).append("},'%')");
            }
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values));
        return params;
    }

    FieldParam fieldParam;
    BeanSearcherProperties config;
    FieldMeta fieldMeta;

    @Override
    public void setFieldParam(FieldParam fieldParam) {
        this.fieldParam = fieldParam;
    }

    @Override
    public FieldParam getFieldParam() {
        return fieldParam;
    }

    @Override
    public BeanSearcherProperties getConfig() {
        return config;
    }

    @Override
    public void setConfig(BeanSearcherProperties config) {
        this.config = config;
    }

    @Override
    public FieldMeta getFieldMeta() {
        return fieldMeta;
    }

    @Override
    public void setFieldMeta(FieldMeta fieldMeta) {
        this.fieldMeta = fieldMeta;
    }
}
