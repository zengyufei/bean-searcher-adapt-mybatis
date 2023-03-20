package com.xunmo.bs.config.bs.op;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.operator.LessThan;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.ObjectUtils;
import com.xunmo.bs.config.bs.FieldOpParam;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

/**
 * 小于运算符
 *
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class MyLessThan extends LessThan implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        final String name = getFieldParam().getName();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
        }
        sqlBuilder.append(" < #{").append(name).append("}");
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
