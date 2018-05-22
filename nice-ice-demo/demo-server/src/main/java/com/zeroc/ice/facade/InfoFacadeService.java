package com.zeroc.ice.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.nice.ice.annotation.IceServer;
import com.nice.ice.demo.facade.info.InfoFacade;
import com.zeroc.Ice.Current;

/**
 * @author Luo Yong
 */
@Component
@IceServer
public class InfoFacadeService implements InfoFacade {

	private static final Logger LOG = LoggerFactory.getLogger(InfoFacadeService.class.getName());

	@Override
	public String testInfo(String infoTitle, String infoContent, Current current) {
		LOG.info(infoTitle + infoContent);
		try {
			Thread.sleep(1300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "testInfo 成功";
	}

	@Override
	public String testException(Current current) {
		if (1 == 1) {
			LOG.info("testException");
			throw new RuntimeException(" this is testException ");
		}
		return "testException";
	}
}
