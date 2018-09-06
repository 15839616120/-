package com.juyoufuli.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:	  ThreadLocalContext. </p>
 * <p>Description:    存储于ThreadLocal的Map, 用于存储上下文. </p>
 *
 * @date     2018/8/28 15:23
 */
public class ThreadLocalContext {

	private static ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			// 降低loadFactory减少冲突
			return new HashMap<String, Object>(16, 0.5f);
		}
	};

	/**
	 * 放入ThreadLocal的上下文信息.
	 */
	public static void put(String key, Object value) {
		contextMap.get().put(key, value);
	}

	/**
	 * 取出ThreadLocal的上下文信息.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) (contextMap.get().get(key));
	}

	/**
	 * 清理ThreadLocal的Context内容.
	 */
	public static void reset() {
		contextMap.get().clear();
	}
}