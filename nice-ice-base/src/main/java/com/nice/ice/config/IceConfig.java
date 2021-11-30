package com.nice.ice.config;

import com.nice.ice.comm.IceConst;
import com.zeroc.Ice.Util;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;

/**
 * 获取配置文件工具类
 *
 * @author Luo Yong
 */
public class IceConfig extends PropertySourcesPlaceholderConfigurer {

    private ConfigurablePropertyResolver propertyResolver = null;
    private com.zeroc.Ice.Properties iceProperties = null;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, ConfigurablePropertyResolver propertyResolver)
        throws BeansException {
        super.processProperties(beanFactory, propertyResolver);
        if (propertyResolver == null) {
            return;
        }
        this.propertyResolver = propertyResolver;
        this.iceProperties = Util.createProperties();

        /**
         * 初始化服务的和客户端配置Key相同的配置
         */

        // 数据传输的最大值，默认4096kb
        setPropertyByKey("Ice.MessageSizeMax", "4096");

        // Only connect to the localhost interface by default.
        setPropertyByKey("Ice.Default.Host", "localhost");

        setThreadPool();
        setOverride();
        setTrace();
        setWarn();
        setSSL();
        setIceMX();
    }

    /**
     * 获取配置文件
     */
    public String get(final String key) {
        return trim(propertyResolver.getProperty(key));
    }

    /**
     * 获取配置文件，为空时，返回默认值
     */
    public String get(final String key, final String defaultValue) {
        String value = trim(propertyResolver.getProperty(key));
        if (IceConst.EMPTY.equals(value)) {
            return defaultValue;
        }
        return value;
    }

    protected com.zeroc.Ice.Properties getIceProperties() {
        return iceProperties;
    }

    protected void setPropertyByKey(final String key) {
        String value = trim(propertyResolver.getProperty(key));
        if (!IceConst.EMPTY.equals(value)) {
            getIceProperties().setProperty(key, value);
        }
    }

    protected void setPropertyByKey(final String key, final String defaultValue) {
        String value = trim(propertyResolver.getProperty(key, defaultValue));
        if (!IceConst.EMPTY.equals(value)) {
            getIceProperties().setProperty(key, value);
        }
    }

    private String trim(final String string) {
        return string == null ? IceConst.EMPTY : string.trim();
    }

    /**
     * Ice 线程池<br />
     * 当线程池中有SizeWarn个活动线程时，会打印"low on threads" 警告<br />
     * SizeWarn的缺省值是SizeMax 值的80%<br />
     * 线程池的大小设置为CPU核心数量的4-16倍
     */
    private void setThreadPool() {
        // 客户端设置
        setPropertyByKey("Ice.ThreadPool.Client.Size", (IceConst.N_CPUS << 2) + IceConst.EMPTY);
        setPropertyByKey("Ice.ThreadPool.Client.SizeMax", (IceConst.N_CPUS << 4) + IceConst.EMPTY);
        setPropertyByKey("Ice.ThreadPool.Client.SizeWarn");
        // 服务端设置
        setPropertyByKey("Ice.ThreadPool.Server.Size", (IceConst.N_CPUS << 2) + IceConst.EMPTY);
        setPropertyByKey("Ice.ThreadPool.Server.SizeMax", (IceConst.N_CPUS << 4) + IceConst.EMPTY);
        setPropertyByKey("Ice.ThreadPool.Server.SizeWarn");
    }

    /**
     * Ice Override 配置<br />
     */
    private void setOverride() {
        // 未知
        setPropertyByKey("Ice.Override.Secure");

        // 如果设置了这个属性，它就会替换所有端点中的超时设置。以毫秒为单位的超时值， -1 是没有超时。
        setPropertyByKey("Ice.Override.Timeout");

        // 这个属性会替换建立连接时用的超时设置。以毫秒为单位的超时值，-1
        // 是没有超时。如果没有设置这个属性，就会使用Ice.Override.Timeout。
        setPropertyByKey("Ice.Override.ConnectTimeout");

        // 如果设置了该属性，就会替换所有代理中的压缩设置。如果被设成大于零的值，就启用压缩。如果设成零，就禁用压缩。
        setPropertyByKey("Ice.Override.Compress");
    }

    /**
     * Ice 跟踪属性
     */
    private void setTrace() {
        // 垃圾收集器跟踪级别：
        // 0 不跟踪垃圾收集器( 缺省)。
        // 1 显示已收集的实例的总数、已检查的实例的总数、在收集器中花费的时间（以毫秒为单位），以及收集器的运行次数。
        // 2 和1 一样，但还在每次运行收集器时生成一条跟踪消息。
        setPropertyByKey("Ice.Trace.GC");

        // 网络跟踪级别:
        // 0 不跟踪网络( 缺省)。
        // 1 跟踪连接的建立和关闭。
        // 2 和1 一样，但更详细。
        // 3 和2 一样，但另外还跟踪数据的传送。
        setPropertyByKey("Ice.Trace.Network");

        // 协议跟踪级别：
        // 0 不跟踪协议( 缺省)。
        // 1 跟踪Ice 的协议消息。
        setPropertyByKey("Ice.Trace.Protocol");

        // 请求重试（request retry）的跟踪级别：
        // 0 不跟踪请求重试( 缺省)。
        // 1 跟踪Ice 操作调用的重试。
        // 2 另外也跟踪Ice 端点的使用。
        setPropertyByKey("Ice.Trace.Retry");

        // 切断的跟踪级别：
        // 0 不跟踪切断活动( 缺省)。
        // 1 跟踪接收者不知道的、因而将被切断的所有异常和类类型。
        setPropertyByKey("Ice.Trace.Slicing");
    }

    /**
     * Ice 警告属性
     */
    private void setWarn() {
        // 如果被设成大于零的值，Ice 应用将针对连接中的某些异常情况，打印出警告消息。缺省值是0。
        setPropertyByKey("Ice.Warn.Connections");

        // 如果被设成大于零的值，服务器将在所收到的数据报超过了服务器的接收缓冲区尺寸时打印警告消息( 注意，并非所有UDP
        // 实现都能检测到这种情况——有些实现会不声不响地扔掉所收到的太大的数据报)。 缺省值是0。
        setPropertyByKey("Ice.Warn.Datagrams");

        // 如果被设成大于零的值， Ice 应用将针对分派请求时发生的某些异常，打印出警告消息。
        // 0 不打印警告。
        // 1 针对意料之外的Ice::LocalException、Ice::UserException、Java runtime 异常，打印警告(
        // 缺省)。
        // 2 和1
        // 一样，但还要针对Ice::ObjectNotExistException、Ice::FacetNotExistException、Ice::OperationNotExistException发出警告。
        setPropertyByKey("Ice.Warn.Dispatch");

        // 如果被设成大于零的值，在某个AMI 回调引发了异常的情况下，打印警告。缺省值是1。
        setPropertyByKey("Ice.Warn.AMICallback");
    }

    /**
     * SSL Configuration<br />
     */
    private void setSSL() {
        setPropertyByKey("Ice.Plugin.IceSSL");
        setPropertyByKey("IceSSL.DefaultDir");
        setPropertyByKey("IceSSL.Keystore");
        setPropertyByKey("IceSSL.Password");
        // SSL 插件跟踪级别：
        // 0 不进行安全性跟踪( 缺省)。
        // 1 跟踪安全性警告。
        // 2 和1 一样，但更详细，包括在配置文件的解析过程中发出的警告。
        setPropertyByKey("IceSSL.Trace.Security");
    }

    /**
     * IceMX configuration.
     */
    private void setIceMX() {
        setPropertyByKey("Ice.Admin.Endpoints");
        setPropertyByKey("Ice.Admin.InstanceName");
        setPropertyByKey("IceMX.Metrics.Debug.GroupBy");
        setPropertyByKey("IceMX.Metrics.ByParent.GroupBy");
    }

}
