package com.nice.ice.annotation;

import java.lang.annotation.*;

/**
 * 发布ice服务注解
 * @author Luo Yong
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IceServer {
}
