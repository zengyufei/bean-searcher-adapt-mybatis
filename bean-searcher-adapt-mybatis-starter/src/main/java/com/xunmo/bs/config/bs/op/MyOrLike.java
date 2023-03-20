package com.xunmo.bs.config.bs.op;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.operator.OrLike;
import cn.zhxu.bs.param.FieldParam;
import com.xunmo.bs.config.bs.FieldOpParam;

import java.util.ArrayList;
import java.util.List;

/**
 * OrLike 运算符
 *
 * @author Troy.Zhou @ 2022-05-23
 * @since v3.7.0
 */
public class MyOrLike extends OrLike implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        final String name = getFieldParam().getName();
        BeanSearcherProperties.Params conf = config.getParams();
        final String separator = conf.getSeparator();
        List<Object> params = new ArrayList<>();
        boolean ic = opPara.isIgnoreCase();
        boolean notFirst = false;
        final Object[] values = opPara.getValues();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value == null) {
                continue;
            }
            if (notFirst) {
                sqlBuilder.append(" or ");
            } else {
                notFirst = true;
            }
            String valKey = name + separator + i;
            if (ic) {
                if (hasILike()) {
                    sqlBuilder.append(fieldSql.getSql());
                    sqlBuilder.append(" ilike #{").append(valKey).append("}");
                } else {
                    toUpperCase(sqlBuilder, fieldSql.getSql());
                    sqlBuilder.append(" like #{").append(valKey).append("}");
                    value = upperCase(value);
                }
            } else {
                sqlBuilder.append(fieldSql.getSql());
                sqlBuilder.append(" like #{").append(valKey).append("}");
            }
            params.addAll(fieldSql.getParas());
            params.add(value);
        }
        return params;
    }

    public static String upperCase(Object value) {
        if (value instanceof String) {
            return ((String) value).toUpperCase();
        }
        return value.toString();
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
