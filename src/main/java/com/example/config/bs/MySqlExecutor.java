package com.example.config.bs;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.SearchSql;
import cn.zhxu.bs.SqlExecutor;
import cn.zhxu.bs.SqlResult;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC Sql 执行器
 *
 * @author Troy.Zhou
 * @since 1.1.1
 */
public class MySqlExecutor implements SqlExecutor {

    protected static final Logger log = LoggerFactory.getLogger(MySqlExecutor.class);

    private SqlSessionFactory sqlSessionFactory;
    private Configuration configuration;
    private SqlBuilderStatement SqlBuilderStatement;

    public MySqlExecutor setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        configuration = sqlSessionFactory.getConfiguration();
        this.SqlBuilderStatement = new SqlBuilderStatement(configuration);
        return this;
    }

    /**
     * 慢 SQL 阈值（单位：毫秒），默认：500 毫秒
     *
     * @since v3.7.0
     */
    private long slowSqlThreshold = 500;

    /**
     * 慢 SQL 监听器
     *
     * @since v3.7.0
     */
    private SlowListener slowListener;


    public MySqlExecutor() {
    }

    public MySqlExecutor(SqlSessionFactory sqlSessionFactory) {
        setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
        if (!searchSql.isShouldQueryList() && !searchSql.isShouldQueryCluster()) {
            return new SqlResult<>(searchSql);
        }

        try {
            return doExecute(searchSql);
        } catch (SQLException e) {
            // 如果有异常，则立马关闭，否则与 SqlResult 一起关闭
            throw new SearchException("A exception occurred when executing sql.", e);
        }
    }

    protected <T> SqlResult<T> doExecute(SearchSql<T> searchSql) throws SQLException {
        SqlResult.ResultSet listResult = null;
        SqlResult.Result clusterResult = null;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            Number totalCount = null;
            if (searchSql.isShouldQueryCluster()) {
                clusterResult = executeClusterSql(sqlSession, searchSql);
                String countAlias = searchSql.getCountAlias();
                if (countAlias != null) {
                    totalCount = (Number) clusterResult.get(countAlias);
                }
            }
            if (searchSql.isShouldQueryList()) {
                if (totalCount == null || totalCount.longValue() > 0) {
                    listResult = executeListSql(sqlSession, searchSql);
                } else {
                    listResult = SqlResult.ResultSet.EMPTY;
                }
            }
        } catch (SQLException e) {
            closeQuietly(clusterResult);
            throw e;
        }
        return new SqlResult<T>(searchSql, listResult, clusterResult) {
            @Override
            public void close() {
                try {
                    super.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected SqlResult.ResultSet executeListSql(SqlSession sqlSession, SearchSql<?> searchSql) throws SQLException {
        final String sourceExecuteSql = searchSql.getListSqlString();
        final List<Object> listSqlParams = searchSql.getListSqlParams();
        LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
        final List<String> all = ReUtil.findAll("(\\#|\\$)\\{(.*?)\\}", sourceExecuteSql, 2);
        for (int i = 0; i < all.size(); i++) {
            final String s = all.get(i);
            valueMap.put(s, listSqlParams.get(i));
        }
        final String listSql = StrUtil.format("<script><![CDATA[{}]]>\n</script>", sourceExecuteSql);
        String listMsId = this.SqlBuilderStatement.selectDynamic(listSql, Map.class);
        Result<Map<String, Object>> result = executeQuery(sqlSession, listMsId, searchSql.getListSqlString(),
                valueMap, searchSql.getBeanMeta());
        return new SqlResult.ResultSet() {
            @Override
            public boolean next() throws SQLException {
                return result.hasNext();
            }

            @Override
            public Object get(String columnLabel) throws SQLException {
                return result.map.get(columnLabel);
            }

            @Override
            public void close() {
                result.close();
            }
        };
    }

    protected SqlResult.Result executeClusterSql(SqlSession sqlSession, SearchSql<?> searchSql) throws SQLException {
        final String sourceCountSql = searchSql.getClusterSqlString();
        final List<Object> clusterSqlParams = searchSql.getClusterSqlParams();
        LinkedHashMap<String, Object> valueMap = new LinkedHashMap<>();
        final List<String> all = ReUtil.findAll("(\\#|\\$)\\{(.*?)\\}", sourceCountSql, 2);
        for (int i = 0; i < all.size(); i++) {
            final String s = all.get(i);
            valueMap.put(s, clusterSqlParams.get(i));
        }
        final String countSql = StrUtil.format("<script><![CDATA[{}]]>\n</script>", sourceCountSql);
        String countMsId = this.SqlBuilderStatement.selectDynamic(countSql, Map.class);
        Result<Map<String, Object>> result = executeQuery(sqlSession, countMsId, searchSql.getClusterSqlString(),
                valueMap, searchSql.getBeanMeta());
        boolean hasValue = result.hasNext();
        return new SqlResult.Result() {
            @Override
            public Object get(String columnLabel) throws SQLException {
                if (hasValue) {
                    return result.map.get(columnLabel);
                }
                return null;
            }

            @Override
            public void close() {
            }
        };
    }

    protected Result<Map<String, Object>> executeQuery(SqlSession sqlSession, String mpId, String sql, Map<String, Object> params,
                                                       BeanMeta<?> beanMeta) throws SQLException {
        long t0 = System.currentTimeMillis();
        try {
            int timeout = beanMeta.getTimeout();
            if (timeout > 0) {
                // 这个方法比较耗时，只在 timeout 大于 0 的情况下才调用它
            }
            final List<Map<String, Object>> selectList = sqlSession.selectList(mpId, params);
            if (CollectionUtils.isNotEmpty(selectList)) {
                final Result<Map<String, Object>> result = new Result<>();
                result.count = selectList.size();
                result.iterator = selectList.iterator();
                return result;
            }
            return new Result<>();
        } finally {
            long cost = System.currentTimeMillis() - t0;
            final Collection<Object> values = params.values();
            afterExecute(beanMeta, sql, new ArrayList<>(values), params, cost);
        }
    }

    protected void afterExecute(BeanMeta<?> beanMeta, String sql, List<Object> params, Map<String, Object> map, long timeCost) {
        if (timeCost >= slowSqlThreshold) {
            Class<?> beanClass = beanMeta.getBeanClass();
            SlowListener listener = slowListener;
            if (listener != null) {
                listener.onSlowSql(beanClass, sql, params, timeCost);
            }
            String replaceSql = StrUtil.replace(sql, "\\#\\{(.*?)\\}", parameter -> "'{" + parameter.group(1) + "}'");
            replaceSql = StrUtil.replace(replaceSql, "\\$\\{(.*?)\\}", parameter -> "{" + parameter.group(1) + "}");
            log.warn("bean-searcher [{}] [{}ms] slow-sql: [{}] on ", beanClass.getName(), timeCost, StrUtil.format(replaceSql, map));
        } else {
            String replaceSql = StrUtil.replace(sql, "\\#\\{(.*?)\\}", parameter -> "'{" + parameter.group(1) + "}'");
            replaceSql = StrUtil.replace(replaceSql, "\\$\\{(.*?)\\}", parameter -> "{" + parameter.group(1) + "}");
            log.debug("bean-searcher [{}ms] sql: {}", timeCost, StrUtil.format(replaceSql, map));
        }
    }

    protected static void closeQuietly(AutoCloseable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (Exception e) {
            log.error("Can not close {}", resource.getClass().getSimpleName(), e);
        }
    }

    public long getSlowSqlThreshold() {
        return slowSqlThreshold;
    }

    /**
     * 设置慢 SQL 阈值（最小慢 SQL 执行时间）
     *
     * @param slowSqlThreshold 慢 SQL 阈值，单位：毫秒
     * @since v3.7.0
     */
    public void setSlowSqlThreshold(long slowSqlThreshold) {
        this.slowSqlThreshold = slowSqlThreshold;
    }

    public SlowListener getSlowListener() {
        return slowListener;
    }

    public void setSlowListener(SlowListener slowListener) {
        this.slowListener = slowListener;
    }

    private static class SqlBuilderStatement {
        private final Configuration configuration;
        private final LanguageDriver languageDriver;

        private SqlBuilderStatement(Configuration configuration) {
            this.configuration = configuration;
            this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        }

        private String newMsId(String sql, SqlCommandType sqlCommandType) {
            return sqlCommandType.toString() + "." + sql.hashCode();
        }

        private boolean hasMappedStatement(String msId) {
            return this.configuration.hasStatement(msId, false);
        }

        private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
            MappedStatement ms = (new MappedStatement.Builder(this.configuration, msId, sqlSource, SqlCommandType.SELECT)).resultMaps(new ArrayList<ResultMap>() {
                {
                    this.add((new ResultMap.Builder(MySqlExecutor.SqlBuilderStatement.this.configuration, "defaultResultMap", resultType, new ArrayList(0))).build());
                }
            }).build();
            this.configuration.addMappedStatement(ms);
        }

        private String select(String sql, Class<?> resultType) {
            String msId = this.newMsId(resultType + sql, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
                this.newSelectMappedStatement(msId, sqlSource, resultType);
            }
            return msId;
        }

        private String select(String sql) {
            String msId = this.newMsId(sql, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
                this.newSelectMappedStatement(msId, sqlSource, Map.class);
            }
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType) {
            String msId = this.newMsId(sql + parameterType, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                SqlSource sqlSource = null;
                try {
                    sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
                } catch (Exception e) {
                    log.error("error sql: {}", sql);
                    throw new RuntimeException(e);
                }
                this.newSelectMappedStatement(msId, sqlSource, Map.class);
            }
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
            String msId = this.newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
                this.newSelectMappedStatement(msId, sqlSource, resultType);
            }
            return msId;
        }
    }


    protected static class Result<T> {

        int count;

        Iterator<T> iterator;

        T map;

        public boolean hasNext() {
            final boolean hasNext = iterator.hasNext();
            if (hasNext) {
                map = iterator.next();
            }
            return hasNext;
        }


        public void close() {
        }

    }
}
