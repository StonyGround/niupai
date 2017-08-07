package com.xiuxiu.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by hzdykj on 2017/7/4.
 */

public class HashMapBeanTools {

    /**
     *  使用reflect(反射)进行转换
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object map2Bean(HashMap<String, String> map, Class<?> beanClass) throws Exception {
        if (map == null)
        {
            return null;
        }
        Object obj = beanClass.newInstance();//新实例

        Field[] fields = obj.getClass().getDeclaredFields(); //获取所有的属性
        for (Field field : fields) {
            int mod = field.getModifiers();//返回此类或接口以整数编码的 Java 语言修饰符
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            field.setAccessible(true);//打破封装
            field.set(obj, map.get(field.getName()));//给obj对象的id属性赋值
        }

        return obj;
    }


    /**
     * HashMap转换成JavaBean
     *
     * @author hailan
     * @param map
     * @param cls
     * @return
     */
    public static Object hashMapToJavaBean(List<HashMap<String, String>> map, Class<?> cls, int j) {
        Object obj = null;
        try {
            obj = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 取出bean里的所有方法
        Method[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; i++) {
            // 取方法名
            String method = methods[i].getName();
            // 取出方法的类型
            Class<?>[] cc = methods[i].getParameterTypes();
            if (cc.length != 1)
                continue;

            // 如果方法名没有以set开头的则退出本次for
            if (!method.startsWith("set") )
                continue;
            // 类型
            String type = cc[0].getSimpleName();

            try {
                //
                Object value = method.substring(3,4).toLowerCase().concat(method.substring(4));
                // 如果map里有该key
                if (map.get(j).containsKey(value)) {
                    // 调用其底层方法
                    setValue(type, map.get(j).get(value), i, methods, obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 调用底层方法设置值
     *
     * @author hailan
     * @param type
     * @param value
     * @param i
     * @param method
     * @param bean
     * @throws Exception
     */
    private static void setValue(String type, Object value, int i, Method[] method,
                                 Object bean) throws Exception {
        if (value != null && !value.equals("")) {
            try {
                if (type.equals("String")) {
                    // 第一个参数:从中调用基础方法的对象 第二个参数:用于方法调用的参数
                    method[i].invoke(bean, new Object[] { value });
                } else if (type.equals("int") || type.equals("Integer")) {
                    method[i].invoke(bean, new Object[] { new Integer(""
                            + value) });
                } else if (type.equals("BigDecimal")) {
                    method[i].invoke(bean, new Object[] { new BigDecimal((String)value) });
                } else if (type.equals("long") || type.equals("Long")) {
                    method[i].invoke(bean,
                            new Object[] { new Long("" + value) });
                } else if (type.equals("boolean") || type.equals("Boolean")) {
                    method[i].invoke(bean, new Object[] { Boolean.valueOf(""
                            + value) });
                } else if (type.equals("Date")) {
                    Date date = null;
                    if (value.getClass().getName().equals("java.util.Date")) {
                        date = (Date) value;
                    } else {
                        //根据文件内的格式不同修改，时间格式太多在此不做通用格式处理。
                        if (value.toString().length() > 10){
                            String format = "yyyy-MM-dd HHmmss";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 10){
                            String format = "yyyy-MM-dd";
                            date = parseDateTime("" + value, format);
                        } else  if (value.toString().length() == 8){
                            String format = "yyyyMMdd";
                            date = parseDateTime("" + value, format);
                        } else  if (value.toString().length() == 14){
                            String format = "yyyyMMddHHmmss";
                            date = parseDateTime("" + value, format);
                        }else  if (value.toString().length() == 6){
                            String format = "HHmmss";
                            date = parseDateTime("" + value, format);
                        }
                    }
                    if (date != null) {
                        method[i].invoke(bean, new Object[] { date });
                    }
                } else if (type.equals("byte[]")) {
                    method[i].invoke(bean,
                            new Object[] { new String(value + "").getBytes() });
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * 日期格式转换
     *
     * @author hailan
     * @param dateValue
     * @param format
     * @return
     */
    private static Date parseDateTime(String dateValue, String format) {
        SimpleDateFormat obj = new SimpleDateFormat(format);
        try {
            return obj.parse(dateValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map<String, Object> hashJavaBeanToMap(Object ojt) {
        Class<?> cls = ojt.getClass();
        Field[] field = cls.getDeclaredFields();

        HashMap<String, Object> mapbean = new HashMap<String, Object>();
        for(int i=0;i<field.length;i++){
            Field f = field[i];
            f.setAccessible(true);
            try {
                mapbean.put(f.getName(), f.get(cls));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return mapbean;
    }
}
