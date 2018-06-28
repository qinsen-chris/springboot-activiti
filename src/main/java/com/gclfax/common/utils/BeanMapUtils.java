package com.gclfax.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * JavaBean and map converter.
 * 
 */
public final class BeanMapUtils {
	
	/**
	 * Converts a map to a JavaBean.
	 * 
	 * @param type type to convert
	 * @param map map to convert
	 * @return JavaBean converted
	 * @throws IntrospectionException failed to get class fields
	 * @throws IllegalAccessException failed to instant JavaBean
	 * @throws InstantiationException failed to instant JavaBean
	 * @throws InvocationTargetException failed to call setters
	 */
	public static final Object toBean(Class<?> type, Map<String, ? extends Object> map)
			throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type);
		Object obj = type.newInstance();
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (map.containsKey(propertyName)) {
				Object value = map.get(propertyName);
				Object[] args = new Object[1];
				args[0] = value;
				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}
	
	/**
	 * Converts a JavaBean to a map.
	 * 
	 * @param bean JavaBean to convert
	 * @return map converted
	 * @throws IntrospectionException failed to get class fields
	 * @throws IllegalAccessException failed to instant JavaBean
	 * @throws InvocationTargetException failed to call setters
	 */
	public static final Map<String, Object> toMap(Object bean)
			throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Map<String, Object> returnMap = new HashMap<>();
		BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result =  readMethod.invoke(bean, new Object[0]);
				if (null != result) {
					returnMap.put(toUpperCase(propertyName), result);
				}
			}
		}
		return returnMap;
	}

	public static Map<String, Object> objectToMap(Object obj) throws Exception {
		if(obj == null)
			return null;

		Map<String, Object> map = new HashMap<String, Object>();

		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String key = property.getName();
			if (key.compareToIgnoreCase("class") == 0) {
				continue;
			}
			Method getter = property.getReadMethod();
			Object value = getter!=null ? getter.invoke(obj) : null;
			if (value!=null){
				map.put(key, value);
			}
		}

		return map;
	}

	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	private static String toUpperCase(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		str = sb.toString(); 
		return str;
	}
	
	/** 
	 * 方法名称:transStringToMap 
	 * 传入参数:mapString 形如 username'chenziwen^password'1234 
	 * 返回值:Map 
	*/  
	public static Map transStringToMap(String mapString){
	  Map map = new HashMap();
	  java.util.StringTokenizer items;
	  for(StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens();
          map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
	      items = new StringTokenizer(entrys.nextToken(), "'");
	  return map;
	}

	/**
	 * 方法名称:transMapToString
	 * 传入参数:map
	 * 返回值:String 形如 username'chenziwen^password'1234
	*/
	public static String transMapToString(Map map){
	  java.util.Map.Entry entry;
	  StringBuffer sb = new StringBuffer();
	  for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
	  {
	    entry = (java.util.Map.Entry)iterator.next();
	      sb.append(entry.getKey().toString()).append( "'" ).append(null==entry.getValue()?"":  
	      entry.getValue().toString()).append (iterator.hasNext() ? "^" : "");  
	  }  
	  return sb.toString();  
	} 
}
