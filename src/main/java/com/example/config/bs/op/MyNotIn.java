package com.example.config.bs.op;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.operator.NotIn;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.ObjectUtils;
import com.example.config.bs.FieldOpParam;

import java.util.ArrayList;
import java.util.List;

/**
 * NotIn 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class MyNotIn extends NotIn implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
        }
        final String name = getFieldParam().getName();
        BeanSearcherProperties.Params conf = config.getParams();
        final String separator = conf.getSeparator();
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        sqlBuilder.append(" not in (");
        for (int i = 0; i < values.length; i++) {
            String valKey = name + separator + i;
            sqlBuilder.append("#{").append(valKey).append("}");
            params.add(values[i]);
            if (i < values.length - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");
        return params;
    }


    FieldParam fieldParam;
    BeanSearcherProperties config;

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

}
