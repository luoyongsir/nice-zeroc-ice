package com.nice.ice.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ICE 服务端动态代理<br/>
 * 暂时未启用，如需启用，放开 IceServerInit 中的注释
 * @author Luo Yong
 */
public class IceInvServer implements InvocationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(IceInvServer.class.getName());
	private com.zeroc.Ice.Object object;

	private IceInvServer(final com.zeroc.Ice.Object target) {
		this.object = target;
	}

	public static com.zeroc.Ice.Object getPrx(final com.zeroc.Ice.Object target) {
		IceInvServer handler = new IceInvServer(target);
		return (com.zeroc.Ice.Object) Proxy.newProxyInstance(handler.getClass().getClassLoader(), target.getClass()
				.getInterfaces(), handler);
	}

	@Override
	public Object invoke(Object o, Method method, Object[] params) {
		try {
			return method.invoke(object, params);
		} catch (Exception e) {
			LOG.error("ICE接口出现异常：", e.getCause());
			// 可以自行封装
			throw new com.zeroc.Ice.UnknownException(e.getCause().toString());
		}
	}
}
