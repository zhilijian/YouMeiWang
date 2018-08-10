package com.youmeiwang.wechat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@CrossOrigin
@RestController
@RequestMapping("/wechat")
public class WeChatController {

	@Autowired
	private WeChatAuthService weChatAuthService;
	
	@RequestMapping(value = "/wxLoginPage",method = RequestMethod.GET)
	public String wxLoginPage() throws Exception {
		String url = weChatAuthService.getAuthorizationUrl();
		// return loginPage(uri);	//此处不明白loginpage()是怎么来的
		JSONObject json = new JSONObject();
		json.put("url", url);
		
		return json.toString();
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.GET)
	public String test() throws Exception {
		String uri = weChatAuthService.getAuthorizationUrl();
		return uri;
	}
	
	@RequestMapping(value = "/wechat")
    public void callback(String code,HttpServletRequest request,HttpServletResponse response) throws Exception {
        String result = weChatAuthService.getAccessToken(code);
        JSONObject jsonObject = JSONObject.parseObject(result);
        
        System.out.println(jsonObject);
/*
 * 
 
        String access_token = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openId");
//      String refresh_token = jsonObject.getString("refresh_token");

        // 保存 access_token 到 cookie，两小时过期
        Cookie accessTokencookie = new Cookie("accessToken", access_token);
        accessTokencookie.setMaxAge(60 *2);
        response.addCookie(accessTokencookie);

        Cookie openIdCookie = new Cookie("openId", openId);
        openIdCookie.setMaxAge(60 *2);
        response.addCookie(openIdCookie);

        //根据openId判断用户是否已经登陆过
        KmsUser user = userService.getUserByCondition(openId);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/student/html/index.min.html#/bind?type="+Constants.LOGIN_TYPE_WECHAT);
        } else {
            //如果用户已存在，则直接登录
            response.sendRedirect(request.getContextPath() + "/student/html/index.min.html#/app/home?open_id=" + openId);
        }
        */
    }
}
