package meikuu.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
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

import meikuu.domain.entity.user.Admin;
import meikuu.domain.entity.work.Topic;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.TopicService;
import meikuu.repertory.service.WorkService;
import meikuu.web.vo.CommonVO;
import meikuu.web.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/topicmanage")
public class TopicManageController {

	@Autowired
	private WorkService workService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/addtopic")
	public CommonVO addTopic(@RequestParam(name="topicName", required=true) String topicName,
			@RequestParam(name="picturePath", required=true) String picturePath,
			@RequestParam(name="describe", required=true) String describe,
			@RequestParam(name="workIDs", required=false) String[] workIDs,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限", "请先核对该用户是否有此权限。");
		}
		
		try {
			Topic topic = topicService.addTopic(topicName, picturePath, describe, workIDs);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("topicID", topic.getTopicID());
			data.put("topicName", topicName);
			data.put("describe", describe);
			return new CommonVO(true, "创建专题成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(true, "创建专题失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/removetopic")
	public SimpleVO removeTopic(@RequestParam(name="topicID", required=true) String topicID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			topicService.removeTopic("topicID", topicID);
			return new SimpleVO(true, "删除专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/batchremovetopic")
	public SimpleVO BatchRemoveTopic(@RequestParam(name="topicIDs", required=true) String[] topicIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			for (String topicID : topicIDs) {
				topicService.removeTopic("topicID", topicID);
			}
			return new SimpleVO(true, "批量删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/edittopic")
	public SimpleVO editTopic(@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="topicName", required=true) String topicName,
			@RequestParam(name="picturePath", required=false) String picturePath,
			@RequestParam(name="describe", required=false) String describe,
			@RequestParam(name="workIDs", required=false) String[] workIDs,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该管理员无此权限。");
		}
		try {
			topicService.setTopic("topicID", topicID, "topicName", topicName);
			if (picturePath != null && !"".equals(picturePath.trim())) {
				topicService.setTopic("topicID", topicID, "picturePath", picturePath);
			}
			if (describe != null && !"".equals(describe.trim())) {
				topicService.setTopic("topicID", topicID, "describe", describe);
			}
			List<String> works = new ArrayList<String>();
			if (workIDs != null && workIDs.length > 0) {
				works = Arrays.asList(workIDs);
			}
			topicService.setTopic("topicID", topicID, "works", works);
			return new SimpleVO(true, "修改专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/toedittopic")
	public CommonVO toEditTopic(@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先申请查看管理员的权限。");
		}
		
		try {
			Topic topic = topicService.queryTopic("topicID", topicID);
			if (topic == null) {
				return new CommonVO(false, "不存在此ID的专题。", "请先核对输入专题ID是否正确。");
			}
			List<Map<String, Object>> worklist = new LinkedList<Map<String, Object>>();
			List<String> works1 = topic.getWorks();
			if (works1 != null) {
				List<String> works2 = new LinkedList<String>();
				int currIdx = (page > 1 ? (page-1)*size : 0);
				for (int i = 0; i < size && i < works1.size()-currIdx; i++) {
					String topicid = works1.get(currIdx + i);
					works2.add(topicid);
				}
				
				for (String workID : works2) {
					Work work = workService.queryWork("workID", workID);
					Map<String, Object> workmap = new HashMap<String, Object>();
					workmap.put("workID", workID);
					workmap.put("workName", work.getWorkName());
					worklist.add(workmap);
				}
			}
			
			Map<String, Object> topicmap = new HashMap<String, Object>();
			topicmap.put("topicID", topicID);
			topicmap.put("topicName", topic.getTopicName());
			topicmap.put("describe", topic.getDescribe());
			topicmap.put("picturePath", topic.getPicturePath());
			topicmap.put("works", worklist);
			
			Long topicAmount = (long) topic.getWorks().size();
			Long pageAmount = 0l;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			Map<String, Object> data = new HashMap <String, Object>();
			data.put("topic", topicmap);
			data.put("topicAmount", topicAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "跳转编辑页面成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "跳转编辑页面失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/topiclist")
	public CommonVO topicList(@RequestParam(name="topicName", required=false) String topicName,
			@RequestParam(name="isRecommend", required=false) Integer isRecommend,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先申请查看管理员的权限。");
		}
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。", "请先核对是否正确输入参数。");
		}
		
		try {
			List<Topic> topiclist1 = topicService.topiclist(topicName, isRecommend);
			List<Topic> topiclist2 = new LinkedList<Topic>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < topiclist1.size()-currIdx; i++) {
				Topic topic = topiclist1.get(currIdx + i);
				topiclist2.add(topic);
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Topic topic : topiclist2) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("topicID", topic.getTopicID());
				map.put("topicName", topic.getTopicName());
				map.put("picturePath", topic.getPicturePath());
				map.put("describe", topic.getDescribe());
				map.put("workNum", topic.getWorks().size());
				map.put("isRecommend", topic.getIsRecommend());
				map.put("createTime", topic.getCreateTime());
				map.put("collected", topic.getCollected()); 
				maplist.add(map);
			}
			
			int topicAmount = topiclist1.size();
			int pageAmount = 0;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap <String, Object>();
			data.put("topics", maplist);
			data.put("topicAmount", topicAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "返回专题列表成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"返回专题列表失败。", "出错信息：" + e.toString());
		}
	}
	
	
	@GetMapping("/addtopicwork")
	public CommonVO addTopicWork(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先申请查看管理员的权限。");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new CommonVO(false, "添加关联模型失败。", "请先核对模型ID输入是否正确。");
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("isDelete", work.getIsDelete());
			return new CommonVO(true, "添加关联模型成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "添加关联模型失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/publishtopic")
	public SimpleVO publishTopic(@RequestParam(name="topicIDs", required=true) String[] topicIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (topicIDs.length != 4) {
			return new SimpleVO(false, "输入的专题数目不为4。");
		}
		try {
			List<Topic> topiclist = topicService.topiclist();
			for (Topic topic : topiclist) {
				boolean isExist = ContainUtil.hasString(topicIDs, topic.getTopicID());
				if (isExist) {
					topicService.setTopic("topicID", topic.getTopicID(), "isRecommend", 1);
				} else {
					topicService.setTopic("topicID", topic.getTopicID(), "isRecommend", 0);
				}
			}
			return new SimpleVO(true, "发布专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/changeisrecommend")
	public SimpleVO changeIsRecommend(@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="isRecommend", required=true) Integer isRecommend,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			switch (isRecommend) {
			case 0:
				topicService.changeIsRecommend(topicID, 0);
				break;
			case 1:
				topicService.changeIsRecommend(topicID, 1);
				break;
			}
			return new SimpleVO(true, "修改推荐状态成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
}
