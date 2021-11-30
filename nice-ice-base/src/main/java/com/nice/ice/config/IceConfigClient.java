package com.nice.ice.config;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Util;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurablePropertyResolver;

/**
 * ice客户端配置文件工具类
 *
 * @author Luo Yong
 */
public class IceConfigClient extends IceConfig {

    private Communicator clientIc;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, ConfigurablePropertyResolver propertyResolver)
        throws BeansException {
        super.processProperties(beanFactory, propertyResolver);
        if (getIceProperties() != null) {
            // 初始化ic
            InitializationData initData = new InitializationData();
            initData.properties = getIceProperties();
            setClientIc(Util.initialize(initData));
        }
    }

    public Communicator getClientIc() {
        return clientIc;
    }

    private void setClientIc(Communicator clientIc) {
        this.clientIc = clientIc;
    }
}
