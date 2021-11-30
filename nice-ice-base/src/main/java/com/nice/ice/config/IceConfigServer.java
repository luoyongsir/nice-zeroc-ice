package com.nice.ice.config;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Util;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurablePropertyResolver;

/**
 * ice服务端配置文件工具类
 *
 * @author Luo Yong
 */
public class IceConfigServer extends IceConfig {

    private Communicator serverIc;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, ConfigurablePropertyResolver propertyResolver)
        throws BeansException {
        super.processProperties(beanFactory, propertyResolver);
        if (getIceProperties() != null) {
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
