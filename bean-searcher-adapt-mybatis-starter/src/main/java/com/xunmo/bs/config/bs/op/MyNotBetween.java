package com.xunmo.bs.config.bs.op;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.operator.Between;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.ObjectUtils;
import cn.zhxu.bs.util.StringUtils;
import com.xunmo.bs.config.bs.FieldOpParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 区间运算符
 *
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class MyNotBetween extends Between implements FieldOpParam {

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        final String name = getFieldParam().getName();
        BeanSearcherProperties.Params conf = config.getParams();
        final String separator = conf.getSeparator();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
        }
        boolean val1Null = false;
        boolean val2Null = false;
        Object value0 = values.length > 0 ? values[0] : null;
        Object value1 = values.length > 1 ? values[1] : null;
        if (value0 == null || (value0 instanceof String && StringUtils.isBlank((String) value0))) {
            val1Null = true;
        }
        if (value1 == null || (value1 instanceof String && StringUtils.isBlank((String) value1))) {
            val2Null = true;
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        if (!val1Null && !val2Null) {
            String valFirstKey = name + separator + "0";
            String valSecondKey = name + separator + "1";
            sqlBuilder.append(" not between #{").append(valFirstKey).append("}");
            sqlBuilder.append(" and #{").append(valSecondKey).append("}");
            params.add(value0);
            params.add(value1);
        } else if (val1Null && !val2Null) {
            String valFirstKey = name + separator + "0";
            sqlBuilder.append(" > #{").append(valFirstKey).append("}");
            params.add(value1);
        } else if (!val1Null) {
            String valSecondKey = name + separator + "1";
            sqlBuilder.append(" < #{").append(valSecondKey).append("}");
            params.add(value0);
        }
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
