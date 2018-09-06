package com.juyoufuli.common.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.*;


/**
 * @ClassName: FastJsonUtils
 * @Description: FastJson工具类
 * @date 2018-06-20
 */
public class FastJsonUtils {

    private static SerializeConfig mapping = new SerializeConfig();

    private static String dateFormat;

    static {
        dateFormat = DateUtils.PATTERN_DEFAULT_ON_SECOND;
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    }


    private static final SerializerFeature[] SERIALIZER_FEATURES = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty};


    /**
     * json字符串转换成对象
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> clazz) {
        try {
            T t = JSON.parseObject(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }


    /**
     * json字符串转换成List集合
     *
     * @param jsonStr
     * @return
     */
    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        try {
            List<T> list = JSON.parseArray(jsonStr, clazz);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * json字符串转换成JSONObject
     *
     * @param jsonStr
     * @return
     */
    public static JSONObject toJSONObject(String jsonStr) {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * json字符串转换成JSONArray
     *
     * @param jsonStr
     * @return
     */
    public static JSONArray toJSONArray(String jsonStr) {
        try {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * json字符串转map集合
     *
     * @param jsonStr
     * @return
     */
    public static HashMap toMap(String jsonStr) {
        try {
            return JSON.parseObject(jsonStr, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 对象转换成json字符串(带特性)
     *
     * @param obj
     * @return
     */
    public static String toJSONWithNullValue(Object obj) {
        try {
            String text = JSON.toJSONString(obj, mapping, SERIALIZER_FEATURES);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}