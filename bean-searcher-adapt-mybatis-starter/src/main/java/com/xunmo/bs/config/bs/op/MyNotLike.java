package com.xunmo.bs.config.bs.op;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.operator.NotLike;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.ObjectUtils;
import com.xunmo.bs.config.bs.FieldOpParam;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

/**
 * NotLike 运算符，用法：
 * <pre>{@code
 * Map<String, Object> params = MapUtils.builder()
 *     .field(User::getName, "张%").op(NotLike.class)
 *     .build();
 * List<User> users = beanSearcher.searchList(User.class, params);
 * // 生成的 SQL：
 * select * from user where name not like '张%'
 * }</pre>
 *
 * @author Troy.Zhou @ 2022-06-19
 * @since v3.8.0
 */
public class MyNotLike extends NotLike implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        final String name = getFieldParam().getName();
        if (opPara.isIgnoreCase()) {
            if (hasILike()) {
                sqlBuilder.append(fieldSql.getSql());
                sqlBuilder.append(" not ilike #{").append(name).append("}");
            } else {
                toUpperCase(sqlBuilder, fieldSql.getSql());
                sqlBuilder.append(" not like #{").append(name).append("}");
                ObjectUtils.upperCase(values);
            }
        } else {
            sqlBuilder.append(fieldSql.getSql()).append(" not like #{").append(name).append("}");
            ;
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
