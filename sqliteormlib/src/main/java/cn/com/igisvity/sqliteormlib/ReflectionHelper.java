/**
 *
 */
package cn.com.igisvity.sqliteormlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhouzhou
 * @Title: ReflectionHelper
 * @Description:增加float  double 支持
 * @Company: www.cari.com.cn
 * @date 2015-9-24 下午4:01:53
 */
public class ReflectionHelper {

    public static void doMethod(String MethodName, Object o, String paras, Class parasType) {
        try {
            Method method = o.getClass().getDeclaredMethod(MethodName, parasType);
            if (parasType.equals(int.class)) {
                method.invoke(o, Integer.parseInt(paras));
            } else if (parasType.equals(double.class)) {
                method.invoke(o, Double.parseDouble(paras));
            } else if (parasType.equals(float.class)) {
                method.invoke(o, Float.parseFloat(paras));
            } else
                method.invoke(o, paras);// 调用o对象的方法
        } catch (NoSuchMethodException ex) {
            ex.fillInStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
