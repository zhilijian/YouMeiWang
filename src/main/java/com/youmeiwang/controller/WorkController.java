package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.VerifyService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.util.RandomUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/work")
public class WorkController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private WorkService workService;
	
	@Autowired
	private VerifyService verifyService;
	
	@PostMapping("/verifyingwork")
	public CommonVO addVerifyingWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workName", required=true) String workName,
			@RequestParam(name="primaryClassification", required=true) Integer primaryClassification,
			@RequestParam(name="pattern", required=true) String pattern,
			@RequestParam(name="hasTextureMapping", required=true) boolean hasTextureMapping,
			@RequestParam(name="isBinding", required=true) boolean isBinding,
			@RequestParam(name="hasCartoon", required=true) boolean hasCartoon,
			@RequestParam(name="price", required=true) Integer price,
			@RequestParam(name="currency", required=true) Integer currency,
			@RequestParam(name="labels", required=true) List<String> labels,
			@RequestParam(name="filePath", required=true) String[] filePath,
			HttpSession session ) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录再操作"); 
//		}
		
		User user = userService.queryUser("userID", userID);
		Work work = new Work();
		String workID = null;
		do {
			workID = RandomUtil.getRandomString(10);
		} while (workService.queryWork("workID", workID) != null);
		work.setWorkID(workID);
		work.setWorkName(workName);
		work.setAuthor(user.getUsername());
		work.setPrimaryClassification(primaryClassification);
		work.setPattern(pattern);
		work.setHasTextureMapping(hasTextureMapping);
		work.setBinding(isBinding);
		work.setBinding(hasCartoon);
		work.setPrice(price);
		work.setCurrency(currency);
		work.setLabels(labels);
		work.setVerifyState(0);
		work.setFilePath(filePath);
		
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("workID", workID);
			data.put("workname", work.getWorkName());
		
			verifyService.addVerifyingWork(userID, workID);
			workService.addWork(work);
			return new CommonVO(true, "添加作品成功！", data);
		} catch (Exception e) {
			return new CommonVO(false, "添加作品失败。", "错误信息：" + e.getMessage());
		} 
	}
	
	@RequestMapping("/verifyWork")
	public CommonVO verifyWork(@RequestParam(name="userID", required=true) String userID,
							@RequestParam(name="workID", required=true) String workID,
							@RequestParam(name="isPass", required=true) boolean isPass, 
							HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录再操作"); 
//		}
		
		User user = userService.queryUser("userID", userID);
		Work work = workService.queryWork("workID", workID);
		CommonVO cvo = new CommonVO();
		try {
			if (isPass) {
				verifyService.verifyAndPassWork(userID, workID);
				cvo.setSuccess(true);
				cvo.setMsg("恭喜，作品审核通过！");
			} else {
				verifyService.verifyNotPassWork(userID, workID);
				cvo.setSuccess(false);
				cvo.setMsg("抱歉，审核未通过。");
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("workID", workID);
			data.put("workname", work.getWorkName());
			data.put("verifyState", work.getVerifyState());
			cvo.setData(data);
			return cvo;
		} catch (Exception e) {
			return new CommonVO(false, "审核时出现错误。","错误信息：" + e.getMessage());
		}
	}
	
	@RequestMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=true) String workID,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		
		User user = userService.queryUser("userID", userID);
		Work work = workService.queryWork("workID", workID);
		
		List<String> verifyingWork = user.getVerifyingWork();
		List<String> verifiedWork = user.getVerifiedWork();
		List<String> notPassWork = user.getNotPassWork();
		List<String> collectWork = user.getCollectWork();
		
		if (verifyingWork.contains(workID)) {
			ListUtil.removeElement(verifyingWork, workID);
		}
		if (verifiedWork.contains(workID)) {
			ListUtil.removeElement(verifiedWork, workID);
		}
		if (notPassWork.contains(workID)) {
			ListUtil.removeElement(notPassWork, workID);
		}
		if (collectWork.contains(workID)) {
			ListUtil.removeElement(collectWork, workID);
		}
		
		work.setAuthor(null);
		
		userService.updateUser(user);
		workService.updateWork(work);
		
		return null;
	}
}
