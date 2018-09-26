package meikuu.repertory.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.alipay.api.domain.AlipayTradePagePayModel;

import meikuu.domain.config.AliPayConfig;
import meikuu.domain.entity.pay.OrderInfo;
import meikuu.domain.util.VerifyUtil;

@Service
public class AliPayService {

	public AlipayTradePagePayModel createModel(OrderInfo order) {
		AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		model.setOutTradeNo(order.getOutTradeNo());
		model.setProductCode("FAST_INSTANT_TRADE_PAY");
		model.setTotalAmount(order.getTotalFee().toString());
		model.setSubject(order.getBody());
		if ("RECHARGE".equals(order.getAttach())) {
			model.setBody("RECHARGE");
		} else {
			model.setBody(order.getProductID());
		}
		return model;
	}
	
	public Map<String, String> receiveOrder(HttpServletRequest request) {
		// 从支付宝回调的request域中取值
		// 获取支付宝返回的参数集合
		Map<String, String[]> aliParams = request.getParameterMap();
		Map<String, String> parameters = new HashMap<String, String>();
		for (Iterator<String> iter = aliParams.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String[] values = aliParams.get(key);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			parameters.put(key, valueStr);
		}
		Map<String, String> result = paramFilter(parameters);
		String orderInfo = createLinkString(result);
		// 验证签名
		String sign = parameters.get("sign");
		if (VerifyUtil.verify(orderInfo, sign, AliPayConfig.ALIPAY_PUBLIC_KEY, true)) {
			return parameters;
		}
//		return null;
		return parameters;
	}
	
	public AlipayTradePagePayModel queryOrCloseModel(String out_trade_no) {
		AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		model.setOutTradeNo(out_trade_no);
		return model;
	} 
	
	/**
	 * 除去数组中的空值和签名参数
	 */
	private Map<String, String> paramFilter(Map<String, String> map) {
		if (map == null || map.size() <= 0) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 */
	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}
}
