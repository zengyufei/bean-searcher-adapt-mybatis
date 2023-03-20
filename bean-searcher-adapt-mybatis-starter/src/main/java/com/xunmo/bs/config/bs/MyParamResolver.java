package com.xunmo.bs.config.bs;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.ParamResolver;
import cn.zhxu.bs.implement.DefaultParamResolver;
import com.xunmo.bs.config.bs.op.MyEqual;

import java.util.List;

/**
 * @author Troy.Zhou @ 2017-03-20
 * <p>
 * 默认查询参数解析器
 */
public class MyParamResolver extends DefaultParamResolver implements ParamResolver {

    public MyParamResolver(List<FieldConvertor.ParamConvertor> convertors, List<ParamFilter> paramFilters) {
        super(convertors, paramFilters);
    }

    @Override
    protected FieldOp allowedOperator(FieldOp op, Class<? extends FieldOp>[] onlyOn) {
        if (op == null) {
            Object tOp = onlyOn.length == 0 ? MyEqual.class : onlyOn[0];
            return getFieldOpPool().getFieldOp(tOp);
        }
        if (onlyOn.length == 0) {
            return op;
        }
        Class<? extends FieldOp> opCls = op.getClass();
        for (Class<? extends FieldOp> clazz : onlyOn) {
            if (clazz == opCls) {
                return op;
            }
        }
        // 不在 onlyOn 的允许范围内时，则使用 onlyOn 中的第一个
        return getFieldOpPool().getFieldOp(onlyOn[0]);
    }

}
