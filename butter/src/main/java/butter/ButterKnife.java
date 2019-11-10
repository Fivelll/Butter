package butter;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;

/**
 * @author lzl
 * Created by lzl on 2019/10/20.
 */

public class ButterKnife {

    public static void binding(Activity activity) {
        //1、获取全限定类名
        String name = activity.getClass().getName();
        try {
            //2、 根据全限定类名获取通过注解解释器生成的Java类，
            Class<?> clazz = Class.forName(name + "_Binding");
            //3、 通过反射获取构造方法并创建实例完成依赖注入
            clazz.getConstructor(activity.getClass()).newInstance(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
