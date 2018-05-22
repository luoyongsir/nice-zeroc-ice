package com;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统启动类
 * @author Luo Yong
 */
@SpringBootApplication
public class BootDemoClient {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BootDemoClient.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}
}
