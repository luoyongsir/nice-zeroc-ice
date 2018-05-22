[["java:package:com.nice.ice.demo.facade"]]
module user {
    interface UserFacade {

        /**
         * 测试用例
         * @author Luo Yong
         * @param userId 用户ID 必填
         * @param userName 用户名称 必填
         * @return 结果封装实体类
         */
        string testUser(long userId, string userName);
    }
}
