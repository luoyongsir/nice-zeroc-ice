# nice-ice-base
ZeroC ICE 结合 Spring 的封装

#### 使用本jar包，可以大大极大简化ice的使用。具体使用方法如下

##### 服务端
+  服务端配置文件中必须包含一个配置ice.endpoints，如下

>Ice.Endpoints=tcp -h 127.0.0.1 -p 5000 -z


+  服务端添加注解如下

>@Component<br/>
>@IceServer<br/>
>public class Xxxxxx implements Demo {<br/>
>// ……<br/>
>}

##### 客户端
+  客户端配置文件中必须包含一个配置Ice.Endpoints.xxx，如下

>Ice.Endpoints.Demo=tcp -h 127.0.0.1 -p 5000 -z<br/>
>Ice.Endpoints.Test=tcp -h 127.0.0.1 -p 6000 -z

+  添加注解如下

>@Component<br/>
>public class Test {
>
>    //……
>
>@IceClient("Demo")<br/>
>private DemoPrx demoPrx;
>
>@IceClient("Test")<br/>
>private TestPrx testPrx;
>
>    //……
>
>}


#### 注意pom文件中依赖的jar包

<br/>
