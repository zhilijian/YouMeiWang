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

import com.youmeiwang.entity.Topic;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.TopicService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.util.RandomUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.ExtraVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/topic")
public class TopicController {

	@Autowired
	private WorkService workService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private TopicService topicService;
	
	@PostMapping("/addtopic")
	public CommonVO addTopic(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="topicName", required=true) String topicName,
							@RequestParam(name="picturePath", required=true) String picturePath,
							@RequestParam(name="describe", required=true) String describe,
							@RequestParam(name="workIDs", required=false) String[] workIDs,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限", "请先核对该用户是否有此权限。");
		}
		
		String topicID = null;
		do {
			topicID = RandomUtil.getRandomNumber(5);
		} while (topicService.queryTopic("topicID", topicID) != null);
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		List<String> works = Arrays.asList(workIDs);
		
		Topic topic = new Topic();
		topic.setTopicID(topicID);
		topic.setTopicName(topicName);
		topic.setPicturePath(picturePath);
		topic.setDescribe(describe);
		topic.setIsRecommend(0);
		topic.setCreateTime(createTime);
		topic.setBrowsed(0l);
		topic.setCollected(0l);
		topic.setWorks(works);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topicID", topicID);
		data.put("topicName", topicName);
		data.put("describe", describe);
		
		try {
			topicService.addTopic(topic);
			return new CommonVO(true, "创建专题成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(true, "创建专题失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/removetopic")
	public SimpleVO removeTopic(@RequestParam(name="adminID", required=true) String adminID,
								@RequestParam(name="topicID", required=true) String topicID,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			topicService.removeTopic("topicID", topicID);
			return new SimpleVO(true, "删除专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/batchremovetopic")
	public SimpleVO BatchRemoveTopic(@RequestParam(name="adminID", required=true) String adminID, 
								@RequestParam(name="topicIDs", required=true) String[] topicIDs,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			topicService.batchRemoveTopic("topicID", topicIDs);
			return new SimpleVO(true, "批量删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@PostMapping("/edittopic")
	public SimpleVO editTopic(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="topicID", required=true) String topicID,
							@RequestParam(name="topicName", required=true) String topicName,
							@RequestParam(name="picturePath", required=false) String picturePath,
							@RequestParam(name="describe", required=false) String describe,
							@RequestParam(name="workIDs", required=false) String[] workIDs,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		List<String> works = new ArrayList<String>();
		if (workIDs != null) {
			works = Arrays.asList(workIDs);
		}
		
		Topic topic = topicService.queryTopic("topicID", topicID);
		topic.setTopicName(topicName);
		topic.setPicturePath(picturePath);
		topic.setDescribe(describe);
		topic.setWorks(works);
		try {
			topicService.updateTopic(topic);
			return new SimpleVO(true, "修改专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/toedittopic")
	public ExtraVO toEditTopic(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="topicID", required=true) String topicID,
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new ExtraVO(false, "该用户尚未登录。", "请先确认是否登录成功。", null);
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new ExtraVO(false, "该用户无此权限。", "请先申请查看管理员的权限。", null);
		}
		try {
			Topic topic = topicService.queryTopic("topicID", topicID);
			if (topic == null) {
				return new ExtraVO(false, "不存在此ID的专题。", "请先核对输入专题ID是否正确。", null);
			}
			List<Map<String, String>> worklist = new LinkedList<Map<String, String>>();
			if (topic.getWorks() != null) {
				for (String workID : topic.getWorks()) {
					Map<String, String> work = new HashMap<String, String>();
					work.put("workID", workID);
					work.put("workName", workService.queryWorkName("workID", workID));
					worklist.add(work);
				}
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("topicID", topicID);
			data.put("topicName", topic.getTopicName());
			data.put("describe", topic.getDescribe());
			data.put("picturePath", topic.getPicturePath());
			data.put("works", worklist);
			
			Long topicAmount = (long) topic.getWorks().size();
			Long pageAmount = 0l;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			Map<String, Object> extra = new HashMap <String, Object>();
			extra.put("topicAmount", topicAmount);
			extra.put("pageAmount", pageAmount);
			return new ExtraVO(true, "跳转编辑页面成功！", data, extra);
		} catch (Exception e) {
			return new ExtraVO(true, "跳转编辑页面失败。", "出错信息：" + e.getMessage(), null);
		}
	}
	
	@GetMapping("/topiclist")
	public ExtraVO topicList(@RequestParam(name="adminID", required=true) String adminID,
							@RequestParam(name="topicName", required=false) String topicName,
							@RequestParam(name="isRecommend", required=false) Integer isRecommend,
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new ExtraVO(false, "该用户尚未登录。", "请先确认是否登录成功。", null);
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new ExtraVO(false, "该用户无此权限。", "请先申请查看管理员的权限。", null);
		}
		
		if (page <= 0 || size <= 0) {
			return new ExtraVO(false, "参数输入不合理。", "请先核对是否正确输入参数。", null);
		}
		
		try {
			List<Topic> topicList = topicService.topicList("topicName", topicName, "isRecommend", isRecommend, page, size);
			Long topicAmount = topicService.getTopicAmount("topicName", topicName, "isRecommend", isRecommend);
			Long pageAmount = 0l;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			for (Topic topic : topicList) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("topicID", topic.getTopicID());
				map.put("topicName", topic.getTopicName());
				map.put("picturePath", topic.getPicturePath());
				map.put("describe", topic.getDescribe());
				map.put("workNum", topic.getWorks().size());
				map.put("isRecommend", topic.getIsRecommend());
				map.put("createTime", topic.getCreateTime());
				map.put("collected", topic.getCollected());
				data.add(map);
			}
			Map<String, Object> extra = new HashMap <String, Object>();
			extra.put("topicAmount", topicAmount);
			extra.put("pageAmount", pageAmount);
			return new ExtraVO(true, "返回专题列表成功！", data, extra);
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtraVO(false,"返回专题列表失败。", "出错信息：" + e.getMessage(), null);
		}
	}
	
	@GetMapping("/changeisrecommend")
	public SimpleVO changeIsRecommend(@RequestParam(name="adminID", required=true) String adminID,
									@RequestParam(name="topicID", required=true) String topicID,
									@RequestParam(name="isRecommend", required=true) Integer isRecommend,
									HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			if (isRecommend == 0) {
				topicService.changeIsRecommend(topicID, 1);
			} else if (isRecommend == 1) {
				topicService.changeIsRecommend(topicID, 0);
			} else {
				return new SimpleVO(false, "参数输入有误。");
			}
			return new SimpleVO(true, "修改推荐状态成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/addtopicwork")
	public CommonVO addTopicWork(@RequestParam(name="adminID", required=true) String adminID,
								@RequestParam(name="workID", required=true) String workID,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先申请查看管理员的权限。");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new CommonVO(false, "添加关联模型失败。", "请先核对模型ID输入是否正确。");
			}
			List<Map<String, String>> data = new LinkedList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("workID", workID);
			map.put("workName", work.getWorkName());
			data.add(map);
			return new CommonVO(true, "添加关联模型成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "添加关联模型失败。", "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/publishtopic")
	public SimpleVO publishTopic(@RequestParam(name="adminID", required=true) String adminID,
								@RequestParam(name="topicIDs", required=true) String[] topicIDs,
								HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 1);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (topicIDs.length != 4) {
			return new SimpleVO(false, "输入的专题数目不为4。");
		}
		try {
			List<Topic> topiclist = topicService.topicList();
			for (Topic topic : topiclist) {
				boolean isExist = ContainUtil.hasString(topicIDs, topic.getTopicID());
				if (isExist) {
					topic.setIsRecommend(1);
				} else {
					topic.setIsRecommend(0);
				}
				topicService.updateTopic(topic);
			}
			return new SimpleVO(true, "发布专题成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(true, "发布专题成功！");
		}
	}
	
	
}
