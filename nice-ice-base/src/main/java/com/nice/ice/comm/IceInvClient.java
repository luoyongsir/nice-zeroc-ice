package com.nice.ice.comm;

import com.zeroc.Ice.ConnectionRefusedException;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.UnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ICE 客户端动态代理<br/>
 * 暂时未启用，如需启用，放开 IceClientInit 中的注释
 * @author Luo Yong
 */
public class IceInvClient implements InvocationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(IceInvClient.class.getName());
	private ObjectPrx object;

	private IceInvClient(final ObjectPrx target) {
		this.object = target;
	}

	public static ObjectPrx getPrx(final ObjectPrx target) {
		IceInvClient handler = new IceInvClient(target);
		return (ObjectPrx) Proxy.newProxyInstance(handler.getClass().getClassLoader(), target.getClass()
				.getInterfaces(), handler);
	}

	@Override
	public Object invoke(Object o, Method method, Object[] params) throws Throwable {
		Object res = null;
		try {
			res = method.invoke(object, params);
		} catch (InvocationTargetException e) {
			// 拦截错误日志，自定义封装返回
			Throwable ex = e.getTargetException();
			if (ex instanceof ConnectionRefusedException) {
				res = "ConnectionRefusedException";
			} else if (ex instanceof UnknownException) {
				UnknownException ue = (UnknownException) ex;
				res = ue.unknown;
			}
		}
		if (res == null) {
			res = "异常默认值";
		}
		if (LOG.isInfoEnabled()) {
			LOG.info(String.valueOf(res));
		}
		return res;
	}
}
