package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.youmeiwang.util.AliyunMessageUtil;
import com.youmeiwang.util.RandomUtil;
import com.youmeiwang.util.WangyiyunMessageUtil;
import com.youmeiwang.vo.CommonVO;

@CrossOrigin
@RestController
@RequestMapping("/msg")
public class MsgController {
	
	@GetMapping("/sendalimsg")
	public CommonVO sendAliMsg(@RequestParam(name="username", required=true) String username, HttpSession session) throws ClientException {
	    
		String code = RandomUtil.getRandomNumber(6);
	    String jsonContent = "{\"code\":\"" + code + "\"}";
	    
	    session.setAttribute("code", code);
	    
	    Map<String, String> paramMap = new HashMap<String, String>();
	    paramMap.put("phoneNumber", username);
	    paramMap.put("msgSign", "游美网");
	    paramMap.put("templateCode", "SMS_114070227");
	    paramMap.put("jsonContent", jsonContent);
	    Map<String, Object> data = new HashMap<String, Object>();
	    data.put("code", code);
	    try {
	    	SendSmsResponse sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
	    	if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    		return new CommonVO(true, "短信发送成功！", data);
	    	} else {
	    		return new CommonVO(false, "短信发送失败。", "出错信息：" + sendSmsResponse.getMessage());
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "短信发送失败。", e.getMessage());
		}
	}
	
	@GetMapping("/sendwangyimsg")
	public CommonVO sendWangyiMsg(@RequestParam(name="phone", required=true) String phone, HttpSession session) {
		String code = RandomUtil.getRandomNumber(6);
		try {
			JSONObject jsonObject = WangyiyunMessageUtil.sendMsg(phone, code);
			if ("200".equals(jsonObject.getString("code"))) {
				return new CommonVO(true, "短信发送成功！", code);
			} else {
				return new CommonVO(false, "短信发送失败。", "状态码：" + jsonObject.getString("code"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "短信发送失败。", "错误信息：" + e.getMessage());
		}
	}
}
