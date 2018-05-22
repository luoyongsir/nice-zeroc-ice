package com.nice.ice.init;

import com.nice.ice.annotation.IceClient;
import com.nice.ice.comm.IceConfigClient;
import com.nice.ice.comm.IceConst;
import com.zeroc.Ice.ObjectPrx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Spring容器完成Bean的实例化之前，查找ICEClient注解，构建代理
 * @author Luo Yong
 */
@Component
public class IceClientInit implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(IceClientInit.class.getName());
	private static final ConcurrentMap<String, Object> LOCK_MAP = new ConcurrentHashMap<>();
	/**
	 * key: ${node}_${className}
	 */
	private static final ConcurrentMap<String, ObjectPrx> IO_CACHE = new ConcurrentHashMap<>();
	@Autowired
	private IceConfigClient iceConfigClient;

	@Bean
	public static IceConfigClient iceConfigClient() {
		IceConfigClient iceConfigClient = new IceConfigClient();
		iceConfigClient.setIgnoreUnresolvablePlaceholders(true);
		iceConfigClient.setIgnoreResourceNotFound(true);
		try {
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:**/ice-config.client");
			iceConfigClient.setLocations(resources);
		} catch (IOException e) {
			LOG.error("初始化服务端配置文件出错：", e);
		}
		return iceConfigClient;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		setIceProxy(bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	private void setIceProxy(Object bean) {
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			final IceClient iceClient = field.getAnnotation(IceClient.class);
			if (iceClient == null) {
				continue;
			}
			field.setAccessible(true);
			try {
				ObjectPrx prx = buildPrxByKey(iceClient.value(), field.getType().getName());
				field.set(bean, prx);
			} catch (IllegalAccessException e) {
				LOG.error("ICE客户端初始化代理出错：", e);
			}
		}
	}

	/**
	 * 获取 ObjectPrx
	 */
	private ObjectPrx buildPrxByKey(final String node, final String key) {
		ObjectPrx prx;
		final StringBuilder bud = new StringBuilder();
		String cacheKey = join(bud, node, IceConst.UNDERLINE, key);
		// 防止相同key重复初始化
		synchronized (getLock(cacheKey)) {
			// 缓存中获取到对象直接返回
			prx = IO_CACHE.get(cacheKey);
			if (prx != null) {
				return prx;
			}
			// 拼接配置文件key
			String nodeKey = join(bud, IceConst.ICE_ENDPOINTS, IceConst.DOT, node);
			String serverUrl = iceConfigClient.get(nodeKey);
			if (IceConst.EMPTY.equals(serverUrl)) {
				LOG.error(" config 里缺少 {} 配置", nodeKey);
				System.exit(1);
			}
			// 拼接stringToProxy
			String stringToProxy = join(bud, key, IceConst.COLON, serverUrl);
			ObjectPrx base = iceConfigClient.getClientIc().stringToProxy(stringToProxy);
			try {
				// 初始化代理对象
				Method method = Class.forName(key).getDeclaredMethod("uncheckedCast", ObjectPrx.class);
				prx = (ObjectPrx) method.invoke(null, base);

//				// 使用动态代理，拦截解析服务端返回的异常
//				prx = IceInvClient.getPrx(prx);

				// 放入缓存
				IO_CACHE.put(cacheKey, prx);
			} catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException
					| InvocationTargetException e) {
				LOG.error("转换代理异常，请检查服务端代码或配置文件和客户端是否一致：", e);
				System.exit(1);
			}
		}
		return prx;
	}

	/**
	 * 拼接字符串
	 */
	private <T> String join(StringBuilder bud, T... elements) {
		bud.setLength(0);
		for (T t : elements) {
			if (t != null) {
				bud.append(t);
			}
		}
		return bud.toString();
	}

	/**
	 * 根据cacheKey 获取锁对象
	 */
	private Object getLock(final String cacheKey) {
		Object newLock = new Object();
		Object oldLock = LOCK_MAP.putIfAbsent(cacheKey, newLock);
		return oldLock != null ? oldLock : newLock;
	}
}
