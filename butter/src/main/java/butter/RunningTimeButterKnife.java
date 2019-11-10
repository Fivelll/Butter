package butter;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.example.lzl.butterknifelzl.Bind;
import com.example.lzl.butterknifelzl.Click;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lzl
 * Created by lzl on 2019/10/28.
 */

public class RunningTimeButterKnife {

    public static void binding(final Activity target){

        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bind.class)){
                Bind bind = field.getAnnotation(Bind.class);
                View view = target.findViewById(bind.value());
                try {
                    field.set(target, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } // end if
        } // end for


        Method[] methods = target.getClass().getDeclaredMethods();
        for (final Method method : methods){
            method.setAccessible(true);
            if (method.isAnnotationPresent(Click.class)){
                Click click = method.getAnnotation(Click.class);
                (target.findViewById(click.value())).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            method.invoke(target);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } //end for
    } // end method
}
