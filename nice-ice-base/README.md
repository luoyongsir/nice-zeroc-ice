# nice-ice-base
ZeroC ICE 基于JDK动态代理 结合 Spring 的封装

#### 使用本jar包，可以极大简化ice的使用。具体使用方法如下

##### 服务端
+ 服务端配置文件中必须包含一个配置Ice.Endpoints，如下

        Ice.Endpoints=tcp -h 127.0.0.1 -p 5000 -z


+ 服务端添加注解如下

        @Component
        @IceServer
        public class Xxxxxx implements Demo {
            // ……
        }

##### 客户端
+ 客户端配置文件中必须包含一个配置Ice.Endpoints.xxx，xxx是用来定位目标服务的

        Ice.Endpoints.Demo=tcp -h 127.0.0.1 -p 5000 -z
        Ice.Endpoints.Test=tcp -h 127.0.0.1 -p 6000 -z

+ 添加注解如下

        @Component
        public class Test {

            @IceClient("Demo")
            private DemoPrx demoPrx;

            @IceClient("Test")
            private TestPrx testPrx;

            //……
        }

#### 注意pom文件中依赖的jar包，请参照nice-ice-demo

<br/>
