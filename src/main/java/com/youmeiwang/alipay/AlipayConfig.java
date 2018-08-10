package com.youmeiwang.alipay;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class AlipayConfig {

	public static final String URL = "https://openapi.alipay.com/gateway.do";
	public static final String APPID = "2016082701812363";
	public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDHSmcAvnwLSkPl" + 
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
	public static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	
	public static final String PID = "2088421963544543";
	public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOpGNomVnRIYP7IT"
			+ "byIdEmBCR5uHuCl2Jg4Gs5Ik7k/WWBnv85ImsC7BVfsX3f1l94104L/J3H6/QvM4"
			+ "7nXpFxC7NwPCIHm6sLbNTwFMcC3i8jQzxKyA5L/qNgaL9D96yvSuTm4huAFfp/qR"
			+ "e2CceNVANJnQ2nB4GgatQcArGkKZAgMBAAECgYArfG8BKPFn+3JPVsYpOeZAUe1C"
			+ "1HV91L2JmvrYJNzLmwjtf5nhxiar9x1Dp5GASN4jts9FKm4wZMZDqYyx/FtYV80z"
			+ "eRqqlywitFByjYC2bUvMSFajDQDpsSIrB0sjWzjbUv7cUeoxxi+jlziO43BF0oLl"
			+ "g41rdCjIaLSm5XK2tQJBAPWf3dEtcVT8KgPPQG4wm8gU9FonQCbr/e1J9FpRCKl/"
			+ "t2s+OaxEtILqd9T8CudMuWPSpMehIV1Ty8JDfc16hQsCQQD0K5uSFIUroTEsVuOH"
			+ "VMNz8E2Wwolg8CLaOTxl/dDaFfcq5qLsYVBuhjY5Lgoji2lZbo/eQOJ8A3oqdecT"
			+ "KFVrAkBUSD78//Lbjot8MymQpe1OgqI2LTG+KUxAmBfYxeWLA+AUVI3Fpu2p3nqw"
			+ "AqxbIeCbeDRCq++e7poEVtRcJaZxAkB/pzjLU40X9Ur3Cmoj+42/1HdMBWK7WnBu"
			+ "NJQ+IkeJbQhu1muBN5NMZUB9/nLwiFdImUQAB14hRdQd1Mw9OM4HAkEA5V13rWfZ"
			+ "p2b1g+p3LaHok5PEbLd28KXg4Y1ifdGwq0PfGNmLgg8eqHpbkJnViN/1zonXycpx" + "vcTzb6VWKE6ujQ==";
	public static final String ALI_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRA"
			+ "FljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQE"
			+ "B/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5Ksi" + "NG9zpgmLCUYuLkxpLQIDAQAB";
	
	
	/** 签名类型 */
	public static final String signType = "RSA2";
    /** 格式 */
	public static final String formate = "json";
    /** 编码 */
	public static final String charset = "UTF-8";
    /** 同步地址 */
//	public static final String returnUrl;
    /** 异步地址 */
//	public static final String notifyUrl;
    /** 最大查询次数 */
	public static final int maxQueryRetry = 5;
    /** 查询间隔（毫秒） */
	public static final long queryDuration = 5000;
    /** 最大撤销次数 */
	public static final int maxCancelRetry = 3;
    /** 撤销间隔（毫秒） */
	public static final long cancelDuration = 3000;
    
//    private AlipayConfig() {};
//    
//    public String description() {
//        StringBuilder sb = new StringBuilder("\nConfigs{");
//        sb.append("支付宝网关: ").append(URL).append("\n");
//        sb.append(", appid: ").append(APPID).append("\n");
//        sb.append(", 商户RSA私钥: ").append(getKeyDescription(APP_PRIVATE_KEY)).append("\n");
//        sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(ALIPAY_PUBLIC_KEY)).append("\n");
//        sb.append(", 签名类型: ").append(signType).append("\n");
//
//        sb.append(", 查询重试次数: ").append(maxQueryRetry).append("\n");
//        sb.append(", 查询间隔(毫秒): ").append(queryDuration).append("\n");
//        sb.append(", 撤销尝试次数: ").append(maxCancelRetry).append("\n");
//        sb.append(", 撤销重试间隔(毫秒): ").append(cancelDuration).append("\n");
//        sb.append("}");
//        return sb.toString();
//    }
//
//    private String getKeyDescription(String key) {
//        int showLength = 6;
//        if (StringUtils.isNotEmpty(key) && key.length() > showLength) {
//            return new StringBuilder(key.substring(0, showLength)).append("******")
//                    .append(key.substring(key.length() - showLength)).toString();
//        }
//        return null;
//    }
    
}
