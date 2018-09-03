package com.youmeiwang.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

@ConfigurationProperties
@EnableConfigurationProperties(AliPayConfig.class)
public class AliPayConfig {

	public static String URL = "https://openapi.alipay.com/gateway.do";
	public static String APPID = "2016082701812363";
	public static String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDHSmcAvnwLSkPl" + 
		"EDTSQgWu8Xmwc52soBXhlcfG3JvZgWqaGHnzEAF9HtnvhfiwBtvmiY2w8R8l3z1j" + 
		"hFJ7Eau53EEDL8k+/AX0tDK+PA8kGNb3vGd1AivMK3Xycu2Xud4ghdyCBTuQh9V5" + 
		"4PzyWq2WKKf1yiHPyP5NPrCpqfWV1aJKVFh8Zz7i+TsI2SLh9+yCjqo56PBk3EV6" + 
		"a+h4Wcdpqx40VKmYhz0Mm1xoMBX/mSeQvbA7lbI6nyeKNXBa2ZK/vT8SneMmrw8T" + 
		"TWpIbYdg4Kq0uek1jEMA00UcDj2LagnOfmaaMY7NdMTDNQMOcjoa92xWK9QVB2II" + 
		"e1ndJEs1AgMBAAECggEAQf5LWZ26aeqvzPyu4SlnxGTGTT4b6nwggtEclKedHEuU" + 
		"x25vg6O7Onmq5XxRu8iprQ/D0eIm1F22cGvy/Dn/yc474RhD5g2C02OQoho1UVjA" + 
		"Zs0G3WJYqoWBy0VvlBzCh7aMLKoa7WNtt6xfVsncTass42GKDMqcG2w1CBn17yV3" + 
		"rjQdD86u6KKjo5lHtzKrWlJnOovukajgW3vDv1fTxuz3hniP5yYF3o/YUKXCj6p0" + 
		"RhO1u8J3uLylAugmsQPcSfh87kAaoDQCqgxjyObCmfqk6fyVTkyqa0ahrJ3uhBqs" + 
		"n8/4IRIP0rKjLYno6NEUO4RNT+EMicY2rY2knKuuQQKBgQDojPTINgIuei92+bj/" + 
		"eZD3HCFu71AYnTDMa0YnRjuHWkOARtwXTOnI08rD89SjzKjnNMSkySV3UF1Y2M7O" + 
		"/+VLH9vodFYVO7qj4BkiJ+AHxxc9sAGrRWs4wXrDzaNfjfwS6sI55VC/Y0icLujf" + 
		"liF//9WjuFhQeeh2VcFXgxeQCQKBgQDbYuAsgQxZPy06TQVYdhTHtrbSP05lYr+e" + 
		"2YtaJ7gPDNMNB8Z+Dn0AqPPO+HMYS6a3BbIs+lnq/maLJ00tz2As8ZI5BluEZowb" + 
		"YvdYRcxsyfifndDnfESDrXxaq8bdHdrbnHSqxYIrxQVfzoix10ZDYe0Xn/4R1YzO" + 
		"6xW14qtUzQKBgQCozcjRnAKxYhgaMONM7x1/gnfyYtytQU0u1Z+y2pRTh/3/m3Af" + 
		"qcD5+mLxIolz7Red3Pvw8eG0Xi2kVJyqPa5ffdzPO8NQ3m54ji8wT+mbO7VvHdfY" + 
		"TyeppyhKAXdZ4WLdCHO8Ou0GQDdwx8xaoEvo8fhOQ343zIYlf9I8h/gOoQKBgQCN" + 
		"9G6lnWMGJJD5VnkNAmrzx1CIvpnVlI31FULMNUMtydiOZ+eSDYHo+Wm5FJ40rE7V" + 
		"woPGAsBIBBEKHYYdDEBpFG7PI0P4afpihikSHyflhNKxRYa1In2azpjWeqwUy4qf" + 
		"6566GU7fT37ovVVJ7cfzNdwaG1Zk589S3F6R8Hd4SQKBgGPIGkHsKhO/rCMqnKDw" + 
		"vt9rPdFMj9GVA9ccGWXbxsrFrUo5JjKyHniN96q+NexZtjO1dJUwAm8n2VSuPUd4" + 
		"75tt5LKtfXLQQN3q6aEBCRxN0N0t7+lKti+7W8gAg6O58iQorhKPB9yFc3tPDeOG" + 
		"sC4wg5nskQflKfn1Tb5tjIXg";
//	public static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBA"
//			+ "QUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV6"
//			+ "4bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM"
//			+ "8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9"
//			+ "zpgmLCUYuLkxpLQIDAQAB";
	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BA"
			+ "QEFAAOCAQ8AMIIBCgKCAQEAx0pnAL58C0pD5RA00kIFrvF5sHOdrKAV4ZX"
			+ "Hxtyb2YFqmhh58xABfR7Z74X4sAbb5omNsPEfJd89Y4RSexGrudxBAy/JP"
			+ "vwF9LQyvjwPJBjW97xndQIrzCt18nLtl7neIIXcggU7kIfVeeD88lqtlii"
			+ "n9cohz8j+TT6wqan1ldWiSlRYfGc+4vk7CNki4ffsgo6qOejwZNxFemvoe"
			+ "FnHaaseNFSpmIc9DJtcaDAV/5knkL2wO5WyOp8nijVwWtmSv70/Ep3jJq8"
			+ "PE01qSG2HYOCqtLnpNYxDANNFHA49i2oJzn5mmjGOzXTEwzUDDnI6GvdsV"
			+ "ivUFQdiCHtZ3SRLNQIDAQAB";
	
	/** 签名类型 */
	public static String SIGNTYPE = "RSA2";
    /** 格式 */
	public static String FORMATE = "json";
    /** 编码 */
	public static String CHARSET = "UTF-8";
    /** 同步地址 */
	public static String RETURNURL = "http://www.baidu.com";
    /** 异步地址 */
	public static String NOTIFYURL = "http://www.linshaocong.cn:8081/alipay/alipaynotify";
    
	@Bean
	public AlipayClient alipayClient(){
		return new DefaultAlipayClient(URL, APPID, APP_PRIVATE_KEY, FORMATE, CHARSET, ALIPAY_PUBLIC_KEY, SIGNTYPE);
	}
	
}
