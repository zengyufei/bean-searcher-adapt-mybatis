package com.example.config.bs;


import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.param.Paging;

/**
 * MySql 方言实现
 *  
 * @author Troy.Zhou
 * 
 * */
public class MyMySqlDialect implements Dialect {

	private String pageName;
	private String pageSizeName;

	public MyMySqlDialect(String pageName, String pageSizeName) {
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
			ret.append(" limit");
			ret.append(" ${").append(offsetName).append("}").append(", ");
			ret.append(" ${").append(sizeName).append("} ");
			wrapper.addPara(paging.getOffset());
			wrapper.addPara(paging.getSize());
		}
		wrapper.setSql(ret.toString());
		return wrapper;
	}

}
