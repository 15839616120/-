package com.juyoufuli.common.collect;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.juyoufuli.common.lang.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;


/**
 * Map工具类，实现 Map <-> Bean 互相转换
 * @version 2015-01-15
 */
public class MapUtils extends org.apache.commons.collections.MapUtils {

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) {
		return new HashMap<K, V>(initialCapacity);
	}

	public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
		return new HashMap<K, V>(map);
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
		return new LinkedHashMap<K, V>(map);
	}

	public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	@SuppressWarnings("rawtypes")
	public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	public static <C, K extends C, V> TreeMap<K, V> newTreeMap(Comparator<C> comparator) {
		return new TreeMap<K, V>(comparator);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
		return new EnumMap<K, V>((type));
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
		return new EnumMap<K, V>(map);
	}

	public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
		return new IdentityHashMap<K, V>();
	}

	///////////////// from JDK Collections的常用构造函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的Map，节约空间.
	 *
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 *
	 * @see java.util.Collections#emptyMap()
	 */
	public static final <K, V> Map<K, V> emptyMap() {
		return Collections.emptyMap();
	}

	/**
	 * 如果map为null，转化为一个安全的空Map.
	 *
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 *
	 * @see java.util.Collections#emptyMap()
	 */
	public static <K, V> Map<K, V> emptyMapIfNull(final Map<K, V> map) {
		return map == null ? (Map<K, V>) Collections.EMPTY_MAP : map;
	}

	/**
	 * 返回一个只含一个元素但结构特殊的Map，节约空间.
	 *
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 *
	 * @see java.util.Collections#singletonMap(Object, Object)
	 */
	public static <K, V> Map<K, V> singletonMap(final K key, final V value) {
		return Collections.singletonMap(key, value);
	}


	//////// 对两个Map进行diff的操作 ///////
	/**
	 * 对两个Map进行比较，返回MapDifference，然后各种妙用.
	 *
	 * 包括key的差集，key的交集，以及key相同但value不同的元素。
	 *
	 * @see com.google.common.collect.MapDifference
	 */
	public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left,
														Map<? extends K, ? extends V> right) {
		return Maps.difference(left, right);
	}

	/**
	 * List<Map<String, V>转换为List<T>
	 * @param clazz
	 * @param list
	 */
	public static <T, V> List<T> toObjectList(Class<T> clazz, List<HashMap<String, V>> list) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, InstantiationException {
		List<T> retList = new ArrayList<T>();
		if (list != null && !list.isEmpty()) {
			for (HashMap<String, V> m : list) {
				retList.add(toObject(clazz, m));
			}
		}
		return retList;
	}
	
	/**
	 * 将Map转换为Object
	 * @param clazz 目标对象的类
	 * @param map 待转换Map
	 */
	public static <T, V> T toObject(Class<T> clazz, Map<String, V> map) throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		T object = clazz.newInstance();
		return toObject(object, map);
	}

	/**
	 * 将Map转换为Object
	 * @param clazz 目标对象的类
	 * @param map 待转换Map
	 * @param toCamelCase 是否去掉下划线
	 */
	public static <T, V> T toObject(Class<T> clazz, Map<String, V> map, boolean toCamelCase) throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		T object = clazz.newInstance();
		return toObject(object, map, toCamelCase);
	}

	/**
	 * 将Map转换为Object
	 * @param object 目标对象的类
	 * @param map 待转换Map
	 */
	public static <T, V> T toObject(T object, Map<String, V> map) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return toObject(object, map, false);
	}

	/**
	 * 将Map转换为Object
	 * @param object 目标对象的类
	 * @param map 待转换Map
	 * @param toCamelCase 是否采用驼峰命名法转换
	 */
	public static <T, V> T toObject(T object, Map<String, V> map, boolean toCamelCase) throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (toCamelCase) {
			map = toCamelCaseMap(map);
		}
		BeanUtils.populate(object, map);
		return object;
	}
	
	/**
	 * 对象转Map
	 * @param object 目标对象
	 * @return 转换出来的值都是String
	 */
	public static Map<String, String> toMap(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return BeanUtils.describe(object);
	}

	/**
	 * 对象转Map
	 * @param object 目标对象
	 * @return 转换出来的值类型是原类型
	 */
	public static Map<String, Object> toNavMap(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return PropertyUtils.describe(object);
	}

	/**
	 * 转换为Collection<Map<K, V>>
	 * @param collection 待转换对象集合
	 * @return 转换后的Collection<Map<K, V>>
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> Collection<Map<String, String>> toMapList(Collection<T> collection) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		if (collection != null && !collection.isEmpty()) {
			for (Object object : collection) {
				Map<String, String> map = toMap(object);
				retList.add(map);
			}
		}
		return retList;
	}

	/**
	 * 转换为Collection,同时为字段做驼峰转换<Map<K, V>>
	 * @param collection 待转换对象集合
	 * @return 转换后的Collection<Map<K, V>>
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> Collection<Map<String, String>> toMapListForFlat(Collection<T> collection) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		if (collection != null && !collection.isEmpty()) {
			for (Object object : collection) {
				Map<String, String> map = toMapForFlat(object);
				retList.add(map);
			}
		}
		return retList;
	}

	/**
	 * 转换成Map并提供字段命名驼峰转平行
	 * @param object 目标对象
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Map<String, String> toMapForFlat(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, String> map = toMap(object);
		return toUnderlineStringMap(map);
	}

	/**
	 * 将Map的Keys去下划线<br>
	 * (例:branch_no -> branchNo )<br>
	 * @param map 待转换Map
	 * @return
	 */
	public static <V> Map<String, V> toCamelCaseMap(Map<String, V> map) {
		Map<String, V> newMap = new HashMap<String, V>();
		for (String key : map.keySet()) {
			safeAddToMap(newMap, StringUtils.camelCase(key), map.get(key));
		}
		return newMap;
	}

	/**
	 * 将Map的Keys转译成下划线格式的<br>
	 * (例:branchNo -> branch_no)<br>
	 * @param map 待转换Map
	 * @return
	 */
	public static <V> Map<String, V> toUnderlineStringMap(Map<String, V> map) {
		Map<String, V> newMap = new HashMap<String, V>();
		for (String key : map.keySet()) {
			newMap.put(StringUtils.uncamelCase(key), map.get(key));
		}
		return newMap;
	}

}
