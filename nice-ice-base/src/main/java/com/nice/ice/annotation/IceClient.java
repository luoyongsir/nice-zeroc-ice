package com.nice.ice.annotation;

import java.lang.annotation.*;

/**
 * ice client 注解
 * @author Luo Yong
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IceClient {

	/**
	 * Ice.Endpoints.${nodeName} 作为配置文件的key <br/>
	 *
	 * 用于客户端选择服务端地址
	 */
	String value() default "default";
}
