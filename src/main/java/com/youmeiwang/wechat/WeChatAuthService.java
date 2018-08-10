package com.youmeiwang.wechat;

import com.alibaba.fastjson.JSONObject;

public interface WeChatAuthService extends AuthService{

	public JSONObject getUserInfo(String accessToken, String openId);
}
