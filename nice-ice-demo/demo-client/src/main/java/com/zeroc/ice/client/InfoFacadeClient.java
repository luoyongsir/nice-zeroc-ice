
package com.zeroc.ice.client;

import com.nice.ice.annotation.IceClient;
import com.nice.ice.demo.facade.info.InfoFacadePrx;
import com.nice.ice.demo.facade.user.UserFacadePrx;
import com.zeroc.ice.comm.Constant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Luo Yong
 */
@Component
public class InfoFacadeClient {

	@IceClient(Constant.DEMO)
	private InfoFacadePrx infoFacadePrx;
	@IceClient(Constant.DEMO)
	private UserFacadePrx userFacadePrx;

	@PostConstruct
	public void test() {
		String res = infoFacadePrx.testInfo("Luo Yong", "testInfo");
		System.out.println(res);
		System.out.println("=====================================");
		System.out.println(userFacadePrx.testUser(12345, "luoyongsir"));
		System.out.println("=====================================");
		System.out.println(infoFacadePrx.testException());
	}
}
