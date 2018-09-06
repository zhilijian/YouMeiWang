package com.youmeiwang.controller;

import java.util.HashMap;
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

import com.youmeiwang.entity.BBS;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.BBSService;
import com.youmeiwang.service.NewsService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/bbs")
public class BBSController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private BBSService bbsService;
	
	@Autowired
	private NewsService newsService;
	
	@PostMapping("/addbbs")
	public CommonVO addBBS(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=false) String workID,
			@RequestParam(name="correctionType", required=false) Integer correctionType,
			@RequestParam(name="comment", required=true) String comment,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先确认该用户是否登录。"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			String username = user.getUsername();
			BBS bbs = bbsService.addBBS(username, workID, correctionType, comment);
			String title = "信息纠错提交成功！";
			String content = "感谢您的信息反馈，我们后台管理人员已接受并将尽快处理您的纠错信息！";
			newsService.addNews(userID, title, content, 1);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbsID", bbs.getBbsID());
			data.put("userID", userID);
			data.put("comment", comment);
			data.put("type", bbs.getType());
			if (workID != null) {
				data.put("workID", workID);
				data.put("correctionType", correctionType);
			} 
			return new CommonVO(true, "添加BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "添加BBS失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/removebbs")
	public SimpleVO removeBBS(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="bbsID", required=true) String bbsID,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new SimpleVO(false, "管理员尚未登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 2);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			bbsService.removeBBS("bbsID", bbsID);
			return new SimpleVO(true, "删除BBS成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/batchremovebbs")
	public SimpleVO batchRemoveBBS(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="bbsIDs", required=true) String[] bbsIDs,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new SimpleVO(false, "管理员尚未登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 2);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			bbsService.batchRemoveBBS("bbsID", bbsIDs);
			return new SimpleVO(true, "批量删除BBS成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
			
	@PostMapping("/editbbs")
	public CommonVO editBBS(@RequestParam(name="userID", required=true) String userID,
						@RequestParam(name="bbsID", required=true) String bbsID,
						@RequestParam(name="workID", required=false) String workID,
						@RequestParam(name="correctionType", required=false) Integer correctionType,
						@RequestParam(name="comment", required=true) String comment,
						HttpSession session) {
		
		if (session.getAttribute(userID) == null) {
			return new CommonVO(false, "用户非法登录。", "请先确认该用户是否登录。"); 
		}
		
		try {
			BBS bbs = bbsService.queryBBS("bbsID", bbsID);
			bbs.setWorkID(workID);
			bbs.setCorrectionType(correctionType);
			bbs.setComment(comment);
			bbsService.updateBBS(bbs);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbsID", bbsID);
			data.put("userID", userID);
			data.put("comment", comment);
			data.put("type", bbs.getType());
			if (workID != null) {
				data.put("workID", workID);
				data.put("correctionType", correctionType);
			} 
			return new CommonVO(true, "编辑BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "编辑BBS失败。", "出错信息：" + e.getMessage()); 
		}
	}
	
	@GetMapping("/querybbs")
	public CommonVO queryBBS(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="bbsID", required=true) String bbsID,
			HttpSession session) {
		
		if (session.getAttribute(userID) == null) {
			return new CommonVO(false, "用户非法登录。", "请先确认该用户是否登录。"); 
		}
		
		try {
			BBS bbs = bbsService.queryBBS("bbsID", bbsID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbsID", bbsID);
			data.put("username", bbs.getUsername());
			data.put("comment", bbs.getComment());
			data.put("type", bbs.getType());
			if (bbs.getType() == 1) {
				data.put("workID", bbs.getWorkID());
				data.put("correctionType", bbs.getCorrectionType());
			} 
			return new CommonVO(true, "查询BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.getMessage()); 
		}
	}
	
	@GetMapping("/bbslist1")
	public CommonVO bbsList1(@RequestParam(name="userID", required=true) String userID,
							@RequestParam(name="type", required=true) Integer type,
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
		if (session.getAttribute(userID) == null) {
			return new CommonVO(false, "用户非法登录。", "请先确认该用户是否登录。"); 
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。","请先核对是否正确输入参数。");
		}
		
		try {
			List<BBS> bbslist = bbsService.bbsList("userID", userID, "type", type, page, size);
			Long bbsAmount = bbsService.getBBSAmount("userID", userID, "type", type);
			Long pageAmount = 0l;
			if (bbsAmount % size == 0) {
				pageAmount = bbsAmount / size;
			} else {
				pageAmount = bbsAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbs", bbslist);
			data.put("bbsAmount", bbsAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询BBS成功！", bbslist);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/bbslist2")
	public CommonVO bbsList2(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="type", required=true) Integer type,
							@RequestParam(name="correctionType", required=false) Integer correctionType,
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "管理员尚未登录。", "请先确认该管理员是否登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 2);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先核对该管理员是否拥有此权限。");
		}
		
		try {
			List<BBS> bbslist = bbsService.bbsList("type", type, "correctionType", correctionType, page, size);
			Long bbsAmount = bbsService.getBBSAmount("type", type, "correctionType", correctionType);
			Long pageAmount = 0l;
			if (bbsAmount % size == 0) {
				pageAmount = bbsAmount / size;
			} else {
				pageAmount = bbsAmount / size + 1;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbs", bbslist);
			data.put("bbsAmount", bbsAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.getMessage());
		}
	}
}
