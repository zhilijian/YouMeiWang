package meikuu.domain.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class WangyiyunMessageUtil {

	private static final String SERVER_URL = "https://api.netease.im/sms/sendcode.action";// 发送验证码的请求路径URL
	private static final String APP_KEY = "3cfbe7757fd7d78f61991ae8252eac76";// 账号
	private static final String APP_SECRET = "dc45ed8e83e5";// 密钥
	private static final String NONCE = "123456";// 随机数
	private static final String MOULD_ID = "3993247";// 模板ID
	private static final String CODE_LEN = "6";// 模板ID
	private static final String URL_ENCODING = "utf-8";// 模板ID

	public static JSONObject sendMsg(String phone, String code) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(SERVER_URL);

		String curTime = String.valueOf((new Date().getTime() / 1000L));
		String checkSum = getCheckSum(APP_SECRET, NONCE, curTime);

		// 设置请求的header
		post.addHeader("AppKey", APP_KEY);
		post.addHeader("Nonce", NONCE);
		post.addHeader("CurTime", curTime);
		post.addHeader("CheckSum", checkSum);
		post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 设置请求参数
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("mobile", phone));
		nameValuePairs.add(new BasicNameValuePair("templateid", MOULD_ID));
		nameValuePairs.add(new BasicNameValuePair("codeLen", CODE_LEN));
		nameValuePairs.add(new BasicNameValuePair("templateid", MOULD_ID));
		nameValuePairs.add(new BasicNameValuePair("authCode", code));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, URL_ENCODING));

		// 执行请求
		HttpResponse response = httpclient.execute(post);

		String responseEntity = EntityUtils.toString(response.getEntity(), "utf-8");

		JSONObject jsonObject = JSON.parseObject(responseEntity);
		return jsonObject;
	}

	// 计算并获取checkSum
	public static String getCheckSum(String appSecret, String nonce, String curTime) {
		return encode("SHA", appSecret + nonce + curTime);
	}

	private static String encode(String algorithm, String value) {
		if (value == null) {
			return null;
		}

		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(value.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder sb = new StringBuilder(len * 2);
		for (int $i = 0; $i < len; $i++) {
			sb.append(HEX_DIGITS[(bytes[$i] >> 4) & 0x0f]);
			sb.append(HEX_DIGITS[bytes[$i] & 0x0f]);
		}
		return sb.toString();
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };
}
