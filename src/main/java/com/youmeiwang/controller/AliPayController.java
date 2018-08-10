package com.youmeiwang.controller;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.youmeiwang.alipay.AlipayConfig;

@Controller
@RequestMapping("/alipay")
public class AliPayController {

	@RequestMapping("/paypage")
    public void gotoPayPage(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws AlipayApiException, IOException {
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL,
				AlipayConfig.APPID,
				AlipayConfig.APP_PRIVATE_KEY,
				AlipayConfig.formate,
				AlipayConfig.charset,
				AlipayConfig.ALIPAY_PUBLIC_KEY,
				AlipayConfig.signType);
		AlipayTradePagePayRequest pagePayRequest =new AlipayTradePagePayRequest();
		// 订单模型  
		AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        String out_trade_no = UUID.randomUUID().toString();
		String productCode = "FAST_INSTANT_TRADE_PAY";
		String total_amount = "0.01";
		String subject = "支付测试";
		model.setOutTradeNo(out_trade_no);
		model.setProductCode(productCode);
		model.setTotalAmount(total_amount);
		model.setSubject(subject);
		pagePayRequest.setBizModel(model);

        // 调用SDK生成表单, 并直接将完整的表单html输出到页面
        String form = "";
        try {
        	form = alipayClient.pageExecute(pagePayRequest).getBody();
		} catch (Exception e) {
			 e.printStackTrace();
		}
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.charset);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
