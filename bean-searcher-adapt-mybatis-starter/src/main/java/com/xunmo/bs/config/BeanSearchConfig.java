package com.xunmo.bs.config;

import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.PageExtractor;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.ParamResolver;
import cn.zhxu.bs.SqlExecutor;
import cn.zhxu.bs.SqlResolver;
import cn.zhxu.bs.boot.BeanSearcherAutoConfiguration;
import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.group.GroupPair;
import cn.zhxu.bs.group.GroupResolver;
import com.xunmo.bs.config.bs.MyMySqlDialect;
import com.xunmo.bs.config.bs.MyParamResolver;
import com.xunmo.bs.config.bs.MySqlExecutor;
import com.xunmo.bs.config.bs.MySqlResolver;
import com.xunmo.bs.config.bs.op.MyBetween;
import com.xunmo.bs.config.bs.op.MyContain;
import com.xunmo.bs.config.bs.op.MyEndWith;
import com.xunmo.bs.config.bs.op.MyEqual;
import com.xunmo.bs.config.bs.op.MyGreaterEqual;
import com.xunmo.bs.config.bs.op.MyGreaterThan;
import com.xunmo.bs.config.bs.op.MyInList;
import com.xunmo.bs.config.bs.op.MyLessEqual;
import com.xunmo.bs.config.bs.op.MyLessThan;
import com.xunmo.bs.config.bs.op.MyNotBetween;
import com.xunmo.bs.config.bs.op.MyNotEqual;
import com.xunmo.bs.config.bs.op.MyNotIn;
import com.xunmo.bs.config.bs.op.MyNotLike;
import com.xunmo.bs.config.bs.op.MyOrLike;
import com.xunmo.bs.config.bs.op.MyStartWith;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@AutoConfigureBefore(BeanSearcherAutoConfiguration.class)
public class BeanSearchConfig {

    @Bean
    @ConditionalOnMissingBean(Dialect.class)
    public Dialect getMysqlSqlDialect(BeanSearcherProperties config) {
        final BeanSearcherProperties.Params params = config.getParams();
        final BeanSearcherProperties.Params.Pagination pagination = params.getPagination();
        return new MyMySqlDialect(pagination.getPage(), pagination.getSize());
//        return new MyPostgreSqlDialect(pagination.getPage(), pagination.getSize());
    }

    @Bean
    @ConditionalOnMissingBean(FieldOpPool.class)
    public FieldOpPool myFieldOpPool(Dialect dialect) {
        List<FieldOp> ops = new ArrayList<>();
        // 添加自己喜欢的字段运算符全部 add 进去即可
        // 这里没添加的运算符将不可用
        ops.add(new MyContain());
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

        final FieldOpPool fieldOpPool = new FieldOpPool(ops);
        fieldOpPool.setDialect(dialect);
        return fieldOpPool;
    }

    @Bean
    @Primary
    public SqlExecutor regMyDefaultSqlExecutor(SqlSessionFactory sqlSessionFactory,
                                               BeanSearcherProperties config) {
        final MySqlExecutor executor = new MySqlExecutor(sqlSessionFactory);
        executor.setSlowSqlThreshold(config.getSql().getSlowSqlThreshold());
        return executor;
    }


    @Bean
    @Primary
    public SqlResolver sqlResolver(Dialect dialect, GroupPair.Resolver groupPairResolver, BeanSearcherProperties config) {
        MySqlResolver resolver = new MySqlResolver(dialect);
        resolver.setConfig(config);
        resolver.setGroupPairResolver(groupPairResolver);
        return resolver;
    }

    @Bean
    @Primary
    public ParamResolver paramResolver(PageExtractor pageExtractor,
                                       FieldOpPool fieldOpPool,
                                       List<ParamFilter> paramFilters,
                                       List<FieldConvertor.ParamConvertor> convertors,
                                       GroupResolver groupResolver,
                                       BeanSearcherProperties config) {
        MyParamResolver paramResolver = new MyParamResolver(convertors, paramFilters);
        paramResolver.setPageExtractor(pageExtractor);
        paramResolver.setFieldOpPool(fieldOpPool);
        BeanSearcherProperties.Params conf = config.getParams();
        paramResolver.setOperatorSuffix(conf.getOperatorKey());
        paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
        paramResolver.setOrderName(conf.getOrder());
        paramResolver.setSortName(conf.getSort());
        paramResolver.setOrderByName(conf.getOrderBy());
        paramResolver.setSeparator(conf.getSeparator());
        paramResolver.setOnlySelectName(conf.getOnlySelect());
        paramResolver.setSelectExcludeName(conf.getSelectExclude());
        BeanSearcherProperties.Params.Group group = conf.getGroup();
        paramResolver.setGexprName(group.getExprName());
        paramResolver.setGroupSeparator(group.getSeparator());
        paramResolver.setGroupResolver(groupResolver);
        return paramResolver;
    }

    /**
     * 为了简化多值参数传递，不是必须的
     * 参考：https://github.com/troyzhxu/bean-searcher/issues/10
     *
     * @return 参数过滤器
     */
    @Bean
    public ParamFilter myParamFilter(BeanSearcherProperties config) {
        final BeanSearcherProperties.Params configParams = config.getParams();
        final String separator = configParams.getSeparator();
        final String operatorKey = configParams.getOperatorKey();
        return new ParamFilter() {

            final String OP_SUFFIX = separator + operatorKey;

            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
                Map<String, Object> newParaMap = new HashMap<>();
                paraMap.forEach((key, value) -> {
                    if (key == null) {
                        return;
                    }
                    boolean isOpKey = key.endsWith(OP_SUFFIX);
                    String opKey = isOpKey ? key : key + OP_SUFFIX;
                    Object opVal = paraMap.get(opKey);
                    if (!Arrays.asList("mv", "il", "bt", "nb", "ol", "ni").contains(StrUtil.trim((CharSequence) opVal))) {
                        newParaMap.put(key, value);
                        return;
                    }
                    if (newParaMap.containsKey(key)) {
                        return;
                    }
                    String valKey = key;
                    Object valVal = value;
                    if (isOpKey) {
                        valKey = key.substring(0, key.length() - OP_SUFFIX.length());
                        valVal = paraMap.get(valKey);
                    }
                    if (strContainDou(valVal)) {
                        try {
                            final List<String> split = StrUtil.split((String) valVal, ",");
                            for (int i = 0; i < split.size(); i++) {
                                final String v = split.get(i);
                                newParaMap.put(valKey + separator + i, StrUtil.trim(v));
                            }
                            newParaMap.put(opKey, opVal);
                            return;
                        } catch (Exception ignore) {
                        }
                    }
                    newParaMap.put(key, value);
                });
                return newParaMap;
            }

            private boolean strContainDou(Object value) {
                if (value instanceof String) {
                    String str = ((String) value).trim();
                    return str.contains(",");
                }
                return false;
            }

        };
    }
}
