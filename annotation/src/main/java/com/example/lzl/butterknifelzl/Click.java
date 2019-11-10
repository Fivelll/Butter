package com.example.lzl.butterknifelzl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lzl
 * Created by lzl on 2019/10/20.
 */

/**
 * 这里运行时注解使用RetentionPolicy.RUNTIME
 * 编译时注解使用RetentionPolicy.SOURCE
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Click {
    int value();
}
