package com.nice.ice.comm;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Util;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Properties;

/**
 * ice服务端配置文件工具类
 * @author Luo Yong
 */
public class IceConfigServer extends IceConfig {

	private Communicator serverIc;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		super.processProperties(beanFactory, props);
		if (getIceProperties() != null) {
//			setPropertyByKey(IceConst.ICE_ENDPOINTS);

			// 初始化ic
			InitializationData initData = new InitializationData();
			initData.properties = getIceProperties();
			setServerIc(Util.initialize(initData));
		}
	}

	public Communicator getServerIc() {
		return serverIc;
	}

	private void setServerIc(Communicator serverIc) {
		this.serverIc = serverIc;
	}
}
