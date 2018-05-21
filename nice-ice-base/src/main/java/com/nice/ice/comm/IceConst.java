package com.nice.ice.comm;

/**
 * ICE 静态常量
 * @author Luo Yong
 */
public final class IceConst {

	public static final String DOT = ".";
	public static final String EMPTY = "";
	public static final String COLON = ":";
	public static final String UNDERLINE = "_";
	/**
	 * ICE 配置常量
	 */
	public static final String ICE_ENDPOINTS = "Ice.Endpoints";

	/** 通过Runtime来获得CPU数量 */
	public static final int N_CPUS = Runtime.getRuntime().availableProcessors();

	private IceConst() {
	}
}
