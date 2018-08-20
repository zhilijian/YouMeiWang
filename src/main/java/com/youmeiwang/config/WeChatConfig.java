package com.youmeiwang.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatConfig {

//	@Value("${wechat.pay.appID}")
	public static String APPID = "wx4f1788d8ac74e1e3";
	
//	@Value("${wechat.pay.mch_ID}")
	public static String MCH_ID = "1433133902";
	
//	@Value("${wechat.pay.key}")
	public static String KEY = "E32D3D18EA874A3E8E1A711D140BD975";
	
//	@Value("${wechat.pay.notify_url}")
	public static String NOTIFY_URL = "http://www.linshaocong.cn:8081/wechatpay/wechatnotify";
	
//	@Value("${wechat.pay.sign_type}")
	public static String SIGN_TYPE = "MD5";
	
//	@Value("${wechat.pay.charset}")
	public static final String CHARSET = "utf-8";
	
//	@Value("${wechat.pay.fee_type}")
	public static String FEE_TYPE = "CNY";
	
//	@Value("${wechat.pay.trade_type}")
	public static String TRADE_TYPE = "NATIVE";
}
