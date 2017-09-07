/**
 *
 */
package cn.com.igisvity.sqliteormlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhouzhou
 * @Title: ReflectionHelper
 * @Description:
 * @Company: www.cari.com.cn
 * @date 2015-9-24 下午4:01:53
 */
public class ReflectionHelper {

    public static void doMethod(String MethodName, Object o, String paras, Class parasType) {
        try {
            Method method = o.getClass().getDeclaredMethod(MethodName, parasType);
            try {
                if (parasType.equals(int.class)) {
                    method.invoke(o, Integer.parseInt(paras));
                } else if (parasType.equals(double.class)) {
                    method.invoke(o, Double.parseDouble(paras));
                } else
                    method.invoke(o, paras);// 调用o对象的方法
            } catch (IllegalAccessException ex) {
                ex.fillInStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.fillInStackTrace();
            } catch (InvocationTargetException ex) {
                ex.fillInStackTrace();
            }
        } catch (NoSuchMethodException ex) {
            ex.fillInStackTrace();
        } catch (SecurityException ex) {
            ex.fillInStackTrace();
        }

    }
}
