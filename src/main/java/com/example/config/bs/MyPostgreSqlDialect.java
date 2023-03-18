package com.example.config.bs;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.param.Paging;

/**
 * PostgreSQL 方言实现
 * @author Troy.Zhou @ 2022-04-19
 * @since v3.6.0
 * */
public class MyPostgreSqlDialect implements Dialect {
	private String pageName;
	private String pageSizeName;

	public MyPostgreSqlDialect(String pageName, String pageSizeName) {
		this.pageName = pageName;
		this.pageSizeName = pageSizeName;
	}

	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		SqlWrapper<Object> wrapper = new SqlWrapper<>();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(fromWhereSql);
		if (paging != null) {
			final String sizeName = this.pageSizeName;
			final String offsetName = this.pageName;
			ret.append(" offset").append(" ${").append(offsetName).append("}");
			ret.append(" limit").append(" ${").append(sizeName).append("} ");
			wrapper.addPara(paging.getOffset());
			wrapper.addPara(paging.getSize());
		}
		wrapper.setSql(ret.toString());
		return wrapper;
	}

	@Override
	public boolean hasILike() {
		return false;
	}
	
}
