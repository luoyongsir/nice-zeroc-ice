[["java:package:com.nice.ice.demo.facade"]]
module info {
    interface InfoFacade {

        /**
         * 测试用例
         * @author Luo Yong
         * @param infoTitle 消息标题 必填
         * @param infoContent 消息内容 必填
         * @return 结果封装实体类
         */
        string testInfo(string infoTitle, string infoContent);

        /**
         * 测试异常处理
         * @author Luo Yong
         * @return 结果封装实体类
         */
        string testException();
    }
}
