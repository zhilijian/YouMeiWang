package meikuu.web.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import meikuu.domain.entity.other.BBS;
import meikuu.domain.entity.user.Admin;
import meikuu.domain.entity.user.User;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.BBSService;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.web.vo.CommonVO;
import meikuu.web.vo.SimpleVO;

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
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/addbbs")
	public CommonVO addBBS(@RequestParam(name="workID", required=false) String workID,
			@RequestParam(name="correctionType", required=false) Integer correctionType,
			@RequestParam(name="comment", required=true) String comment,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录。", "{}"); 
		}
		
		try {
			BBS bbs = bbsService.addBBS(userID, workID, correctionType, comment);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbsID", bbs.getBbsID());
			data.put("userID", userID);
			data.put("comment", comment);
			data.put("type", bbs.getType());
			
			String title = "";
			String content = "";
			if (workID != null) {
				data.put("workID", workID);
				data.put("correctionType", correctionType);
				title = "信息纠错提交成功！";
				content = "感谢您的信息反馈，我们后台管理人员已接受并将尽快处理您的纠错信息！";
			} else {
				title = "留言信息提交成功！";
				content = "感谢您的信息反馈，我们后台管理人员已接受并将会考虑您宝贵的建议！";
			}
			newsService.addNews(userID, title, content, 1);
			return new CommonVO(true, "添加BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "添加BBS失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/removebbs")
	public SimpleVO removeBBS(@RequestParam(name="bbsID", required=true) String bbsID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getUserManage(), 2);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			bbsService.removeBBS("bbsID", bbsID);
			return new SimpleVO(true, "删除BBS成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/batchremovebbs")
	public SimpleVO batchRemoveBBS(@RequestParam(name="bbsIDs", required=true) String[] bbsIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getUserManage(), 2);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			for (String bbsID : bbsIDs) {
				bbsService.removeBBS("bbsID", bbsID);
			}
			return new SimpleVO(true, "批量删除BBS成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/bbsdetail")
	public CommonVO BBSdetail(@RequestParam(name="bbsID", required=true) String bbsID, 
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录。", "{}"); 
		}
		
		try {
			BBS bbs = bbsService.queryBBS("bbsID", bbsID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbsID", bbsID);
			data.put("userID", bbs.getUserID());
			data.put("comment", bbs.getComment());
			data.put("type", bbs.getType());
			if (bbs.getType() == 1) {
				data.put("workID", bbs.getWorkID());
				data.put("correctionType", bbs.getCorrectionType());
			} 
			return new CommonVO(true, "查询BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.toString()); 
		}
	}
	
	@GetMapping("/bbslist1")
	public CommonVO bbsList1(@RequestParam(name="type", required=true) Integer type,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录。", "{}"); 
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。","请先核对是否正确输入参数。");
		}
		
		try {
			List<BBS> bbslist = bbsService.bbslist("userID", userID, "type", type, page, size);
			Long bbsAmount = bbsService.getBBSAmount("userID", userID, "type", type);
			Long pageAmount = 0l;
			if (bbsAmount % size == 0) {
				pageAmount = bbsAmount / size;
			} else {
				pageAmount = bbsAmount / size + 1;
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (BBS bbs : bbslist) {
				Map<String, Object> bbsmap = new HashMap<String, Object>();
				bbsmap.put("bbsID", bbs.getBbsID());
				bbsmap.put("userID", bbs.getUserID());
				if (type == 1) {
					bbsmap.put("workID", bbs.getWorkID());
					bbsmap.put("correctionType", bbs.getCorrectionType());
				}
				bbsmap.put("comment", bbs.getComment());
				bbsmap.put("type", bbs.getType());
				bbsmap.put("publishTime", bbs.getPublishTime());
				maplist.add(bbsmap);
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbs", maplist);
			data.put("bbsAmount", bbsAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/bbslist2")
	public CommonVO bbsList2(@RequestParam(name="type", required=true) Integer type,
			@RequestParam(name="correctionType", required=false) Integer correctionType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getUserManage(), 2);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先核对该管理员是否拥有此权限。");
		}
		
		try {
			List<BBS> bbslist = bbsService.bbslist("type", type, "correctionType", correctionType, page, size);
			Long bbsAmount = bbsService.getBBSAmount("type", type, "correctionType", correctionType);
			Long pageAmount = 0l;
			if (bbsAmount % size == 0) {
				pageAmount = bbsAmount / size;
			} else {
				pageAmount = bbsAmount / size + 1;
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (BBS bbs : bbslist) {
				Map<String, Object> bbsmap = new HashMap<String, Object>();
				bbsmap.put("bbsID", bbs.getBbsID());
				bbsmap.put("userID", bbs.getUserID());
				bbsmap.put("comment", bbs.getComment());
				bbsmap.put("type", bbs.getType());
				bbsmap.put("publishTime", bbs.getPublishTime());
				if (type == 1) {
					bbsmap.put("workID", bbs.getWorkID());
					bbsmap.put("correctionType", bbs.getCorrectionType());
				}
				maplist.add(bbsmap);
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bbs", maplist);
			data.put("bbsAmount", bbsAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询BBS成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询BBS失败。", "出错信息：" + e.toString());
		}
	}
}
