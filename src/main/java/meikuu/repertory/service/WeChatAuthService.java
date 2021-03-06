package meikuu.repertory.service;

import com.alibaba.fastjson.JSONObject;

public interface WeChatAuthService extends AuthService{

	public JSONObject getUserInfo(String accessToken, String openId);
	
	public JSONObject getUserInfo(String code);
}
