package com.nice.ice.init;

import com.nice.ice.annotation.IceServer;
import com.nice.ice.comm.IceConst;
import com.nice.ice.config.IceConfigServer;
import com.zeroc.Ice.ObjectAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * ice 服务初始化
 * @author Luo Yong
 */
@Component
public class IceServerInit implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(IceServerInit.class.getName());
	private static final String ADAPTER_NAME = "SystemServerAdapter";
	private ApplicationContext context;
	@Autowired
	private IceConfigServer iceConfigServer;

	@Bean
	public static IceConfigServer iceConfigServer() {
		IceConfigServer iceConfigServer = new IceConfigServer();
		iceConfigServer.setIgnoreUnresolvablePlaceholders(true);
		iceConfigServer.setIgnoreResourceNotFound(true);
		try {
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:**/ice-config.server");
			iceConfigServer.setLocations(resources);
		} catch (IOException e) {
			LOG.error("初始化服务端配置文件出错：", e);
		}
		return iceConfigServer;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
		initIceServer();
	}

	/**
	 * 启动ICE服务，支持多组件服务
	 */
	private void initIceServer() {
		long start = System.currentTimeMillis();
		Map<String, java.lang.Object> serviceMap = context.getBeansWithAnnotation(IceServer.class);
		ObjectAdapter adapter = null;
		for (Map.Entry<String, Object> entry : serviceMap.entrySet()) {
			boolean b = entry.getValue() instanceof com.zeroc.Ice.Object;
			if (!b) {
				continue;
			}
			if (adapter == null) {
				adapter = createAdapter();
			}
			// 客户端调用时需要和这个ice key对应
			String iceKey = entry.getValue().getClass().getInterfaces()[0].getName() + "Prx";
			com.zeroc.Ice.Object obj = (com.zeroc.Ice.Object) entry.getValue();
//			// 使用动态代理拦截封装异常
//			com.zeroc.Ice.Object obj = IceInvServer.getPrx((com.zeroc.Ice.Object) entry.getValue());
			adapter.add(obj, com.zeroc.Ice.Util.stringToIdentity(iceKey));
		}
		if (adapter != null) {
			adapter.activate();
			long useTime = System.currentTimeMillis() - start;
			LOG.info(" ---------- ice server start successfully! start time: {} ms ---------- ", useTime);
			// wait for shutdown
			iceConfigServer.getServerIc().waitForShutdown();
		}
	}

	/**
	 * 初始化 ObjectAdapter
	 */
	private ObjectAdapter createAdapter() {
		String endpoints = iceConfigServer.get(IceConst.ICE_ENDPOINTS);
		return iceConfigServer.getServerIc().createObjectAdapterWithEndpoints(ADAPTER_NAME, endpoints);
	}
}
