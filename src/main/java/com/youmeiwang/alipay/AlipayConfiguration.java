package com.youmeiwang.alipay;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(AliPayConfig.class)
public class AlipayConfiguration {

//	@Bean
//	public AlipayClient alipayClient(){
//		return new DefaultAlipayClient(AlipayConfig.URL,
//				AlipayConfig.APPID,
//				AlipayConfig.ALIPAY_PUBLIC_KEY,
//				AlipayConfig.formate,
//				AlipayConfig.charset,
//				AlipayConfig.ALIPAY_PUBLIC_KEY,
//				AlipayConfig.signType);
//	}
}
