package com.zeroc.ice.facade;

import com.nice.ice.annotation.IceServer;
import com.nice.ice.demo.facade.user.UserFacade;
import com.zeroc.Ice.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Luo Yong
 */
@Component
@IceServer
public class UserFacadeService implements UserFacade {

	private static final Logger LOG = LoggerFactory.getLogger(UserFacadeService.class.getName());

	@Override
	public String testUser(long userId, String userName, Current current) {
		LOG.info("userId" + userId);
		LOG.info("userName" + userName);
		return "testUser 成功！";
	}
}
