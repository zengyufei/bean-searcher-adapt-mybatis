package com.xunmo.bs.config.bs;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlSnippet;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.group.Group;
import cn.zhxu.bs.implement.DefaultSqlResolver;
import cn.zhxu.bs.param.FieldParam;

import java.util.List;
import java.util.Map;

/**
 * 默认 SQL 解析器
 *
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.1.1
 */
public class MySqlResolver extends DefaultSqlResolver {

    BeanSearcherProperties config;

    public MySqlResolver() {
    }

    public MySqlResolver(Dialect dialect) {
        super(dialect);
    }

    public MySqlResolver setConfig(BeanSearcherProperties config) {
        this.config = config;
        return this;
    }

    @Override
    protected void useGroup(Group<List<FieldParam>> group, BeanMeta<?> beanMeta, List<String> fetchFields, Map<String, Object> paraMap,
                            StringBuilder sqlBuilder, List<Object> paraReceiver, boolean isHaving) {
        group.forEach(event -> {
            if (event.isGroupStart()) {
                sqlBuilder.append("(");
                return;
            }
            if (event.isGroupEnd()) {
                sqlBuilder.append(")");
                return;
            }
            if (event.isGroupAnd()) {
                sqlBuilder.append(" and ");
                return;
            }
            if (event.isGroupOr()) {
                sqlBuilder.append(" or ");
                return;
            }
            List<FieldParam> params = event.getValue();
            for (int i = 0; i < params.size(); i++) {
                if (i == 0) {
                    sqlBuilder.append("(");
                } else {
                    sqlBuilder.append(" and (");
                }
                FieldParam param = params.get(i);
                FieldOp.OpPara opPara = new FieldOp.OpPara(
                        (name) -> {
                            String field = name != null ? name : param.getName();
                            FieldMeta meta = beanMeta.requireFieldMeta(field);
                            SqlSnippet sql = meta.getFieldSql();
                            //如果是 group by having 且 Select 列表中 存在该字段，则使用该字段的别名
                            if (isHaving && fetchFields.contains(field)) {
                                sql = new SqlSnippet(meta.getDbAlias());
                            }
                            return resolveDbFieldSql(sql, paraMap);
                        },
                        param.isIgnoreCase(),
                        param.getValues()
                );
                FieldOp operator = (FieldOp) param.getOperator();
                if (operator instanceof DialectWrapper) {
                    ((DialectWrapper) operator).setDialect(this.getDialect());
                }
                if (operator instanceof FieldOpParam) {
                    final FieldOpParam fieldOpParam = (FieldOpParam) operator;
                    fieldOpParam.setFieldParam(param);
                    fieldOpParam.setConfig(config);

                    String field = param.getName();
                    FieldMeta meta = beanMeta.requireFieldMeta(field);
                    fieldOpParam.setFieldMeta(meta);
                }
                final List<Object> operate = operator.operate(sqlBuilder, opPara);
                paraReceiver.addAll(operate);
                sqlBuilder.append(")");
            }
        });
    }

}
