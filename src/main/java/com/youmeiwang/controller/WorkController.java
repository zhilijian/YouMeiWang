package com.youmeiwang.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@PostMapping("/addverifyingwork")
	public CommonVO addVerifyingWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workName", required=true) String workName,
			@RequestParam(name="primaryClassification", required=true) Integer primaryClassification,
			@RequestParam(name="pattern", required=true) Integer[] pattern,
			@RequestParam(name="hasTextureMapping", required=true) boolean hasTextureMapping,
			@RequestParam(name="isBinding", required=true) boolean isBinding,
			@RequestParam(name="hasCartoon", required=true) boolean hasCartoon,
			@RequestParam(name="price", required=true) Integer price,
			@RequestParam(name="labels", required=true) String[] labels,
//			@RequestParam(name="filePath", required=true) String[] filePath,
			HttpSession session ) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录再操作"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			String workID = null;
			do {
				workID = RandomUtil.getRandomString(10);
			} while (workService.queryWork("workID", workID) != null);
			
			Work work = new Work();
			work.setWorkID(workID);
			work.setWorkName(workName);
			work.setAuthor(user.getUsername());
			work.setPrimaryClassification(primaryClassification);
			work.setPattern(Arrays.asList(pattern));
			work.setHasTextureMapping(hasTextureMapping);
			work.setBinding(isBinding);
			work.setBinding(hasCartoon);
			work.setPrice(price);
			work.setLabels(Arrays.asList(labels));
			work.setVerifyState(0);
//			work.setFilePath(Arrays.asList(filePath));
			work.setUploadTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			
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
	
	@GetMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=true) String workID,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		try {
			User user = userService.queryUser("userID", userID);
			Work work = workService.queryWork("workID", workID); 
			String username = userService.queryUser("userID", userID).getUsername();
			String author = workService.queryWork("workID", workID).getAuthor();
			if (!username.equals(author)) {
				return new SimpleVO(false, "非作者无法删除该作品。"); 
			}
			switch (work.getVerifyState()) {
			case 0:
				user.setVerifyingWork(ListUtil.removeElement(user.getVerifyingWork(), workID));
				break;
			case 1:
				user.setVerifiedWork(ListUtil.removeElement(user.getVerifiedWork(), workID));
				break;
			case 2:
				user.setNotPassWork(ListUtil.removeElement(user.getNotPassWork(), workID));
				break;
			default:
				break;
			}
			
			work.setAuthor(null);
			userService.updateUser(user);
			workService.updateWork(work);
			return new SimpleVO(true, "删除作品成功。"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=true) String workID,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
			
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null || work.getAuthor() == null) {
				return new CommonVO(false, "不存在此ID的作品或者该作品已被作者删除", "请查看其他作品。");
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("primaryClassification", work.getPrimaryClassification());
			data.put("secondaryClassification", work.getSecondaryClassification());
			data.put("reclassify", work.getReclassify());
			data.put("pattern", work.getPattern());
			data.put("hasTextureMapping", work.isHasTextureMapping());
			data.put("isBinding", work.isBinding());
			data.put("hasCartoon", work.isHasCartoon());
			data.put("price", work.getPrice());
			data.put("labels", work.getLabels());
			data.put("remarks", work.getRemarks());
			data.put("picturePath", work.getPicturePath());
			data.put("fileNameAndPath", work.getFileNameAndPath());
			return new CommonVO(true, "查看作品成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查看作品失败。", "出错信息：" + e.toString());
		}	
	}
	
	@GetMapping("/worklist")
	public CommonVO workList(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workType", required=true) Integer workType,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录后进行操作"); 
//		}
		
		if (workType < 0 || workType > 4) {
			return new CommonVO(false, "作品类型指数输入错误。", "请重新输入正确的作品类型指数。"); 
		}
		
		try {
			User user = userService.queryUser("userID", userID);
			List<String> workIDs = new ArrayList<String>();
			switch (workType) {
			case 0:
				workIDs = user.getVerifyingWork();
				break;
			case 1:
				workIDs = user.getVerifiedWork();
				break;
			case 2:
				workIDs = user.getNotPassWork();
				break;
			case 3:
				workIDs = user.getCollectWork();
				break;
			case 4:
				workIDs = user.getDownWork();
				break;
			default:
				break;
			}
			
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			if (workIDs ==null) {
				return new CommonVO(true, "该用户无此类作品。", "{}");
			}
			
			for (String workID : workIDs) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				Work work = workService.queryWork("workID", workID);
				if (work == null) {
					continue;
				}
				workmap.put("workID", workID);
				workmap.put("workName", work.getWorkName());
				workmap.put("modelSize", work.getModelSize());
				workmap.put("uploadTime", work.getUploadTime());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
				data.add(workmap);
			}
			return new CommonVO(true, "作品展示成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "作品展示失败。", "出错信息：" + e.toString());
		}
	}
}
