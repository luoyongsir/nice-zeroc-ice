[["java:package:com.nice.ice.demo.facade"]]
module demo {
    interface Hello {

        /**
          * idempotent 关键字指出了一个操作能够安全的执行多次
          * @author Luo Yong
          * @param msg 内容
          * @return
          */
        idempotent void sayHello(string msg);
    }
}
