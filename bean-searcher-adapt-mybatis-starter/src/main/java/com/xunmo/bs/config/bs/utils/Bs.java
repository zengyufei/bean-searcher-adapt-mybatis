package com.xunmo.bs.config.bs.utils;

import cn.zhxu.bs.util.FieldFns;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Bs {

	/**
	 * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象
	 * @param map 已有 Map 参数
	 * @return Map 对象
	 */
	public static Map<String, Object> flat(Map<String, String[]> map) {
		Map<String, Object> newMap = new HashMap<>();
		for (Entry<String, String[]> entry: map.entrySet()) {
			String[] values =entry.getValue();
			if (values.length > 0) {
				newMap.put(entry.getKey(), values[0]);
			}
		}
		return newMap;
	}

	/**
	 * 返回一个 lambda Map 参数构造器
	 * @return MyMapBuilder
	 */
	public static MyMapBuilder builder() {
		return builder(new HashMap<>());
	}

	/**
	 * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象，并返回一个 lambda Map 参数构造器
	 * @param map 已有 Map 参数
	 * @return MyMapBuilder
	 */
	public static MyMapBuilder flatBuilder(Map<String, String[]> map) {
		return new MyMapBuilder(flat(map));
	}

	/**
	 * 返回一个 lambda Map 参数构造器
	 * @param map 已有 Map 参数
	 * @return MyMapBuilder
	 */
	public static MyMapBuilder builder(Map<String, Object> map) {
		return new MyMapBuilder(map);
	}

	/**
	 * 构建只有一个键值对的 Map 对象
	 * @param key 键
	 * @param value 值
	 * @return Map 对象
	 * @since v4.0.0
	 */
	public static Map<String, Object> of(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	/**
	 * 构建只有两个键值对的 Map 对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @return Map 对象
	 * @since v4.0.0
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2) {
		Map<String, Object> map = new HashMap<>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

	/**
	 * 构建只有一个键值对的 Map 对象
	 * @param key 键
	 * @param value 值
	 * @return Map 对象
	 * @since v4.0.0
	 */
	public static <T> Map<String, Object> of(FieldFns.FieldFn<T, ?> key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(FieldFns.name(key), value);
		return map;
	}

	/**
	 * 构建只有两个键值对的 Map 对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @return Map 对象
	 * @since v4.0.0
	 */
	public static <T> Map<String, Object> of(FieldFns.FieldFn<T, ?> key1, Object value1, FieldFns.FieldFn<T, ?> key2, Object value2) {
		Map<String, Object> map = new HashMap<>();
		map.put(FieldFns.name(key1), value1);
		map.put(FieldFns.name(key2), value2);
		return map;
	}

}
