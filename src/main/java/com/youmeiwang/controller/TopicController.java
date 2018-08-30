package com.youmeiwang.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.TopicService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/topic")
public class TopicController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WorkService workService;
	
	@Autowired
	private TopicService topicService;
	
	@GetMapping("/topicdetail")
	public CommonVO topicDetail(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		Topic topic = topicService.queryTopic("topicID", topicID);
		topicService.setTopic("topicID", topicID, "browsed", topic.getBrowsed() + 1);
		List<String> worklist1 = topic.getWorks();
		List<String> worklist2 = new LinkedList<String>();
		int currIdx = (page > 1 ? (page-1)*size : 0);
		for (int i = 0; i < size && i < worklist1.size()-currIdx; i++) {
			String workID = worklist1.get(currIdx + i);
			worklist2.add(workID);
		}
		List<Map<String, Object>> works = new LinkedList<Map<String, Object>>();
		for (String workID : worklist2) {
			Map<String, Object> workmap = new HashMap<String, Object>();
			Work work = workService.queryWork("workID", workID);
			workmap.put("workID", workID);
			workmap.put("workName", work.getWorkName());
			workmap.put("yijifenlei", work.getYijifenlei());
			workmap.put("price", work.getPrice());
			works.add(workmap);
		}
		Long workAmount = (long) worklist1.size();
		Long pageAmount = 0l;
		if (workAmount % size == 0) {
			pageAmount = workAmount / size;
		} else {
			pageAmount = workAmount / size + 1;
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topicID", topicID);
		data.put("topicName", topic.getTopicName());
		data.put("picturePath", topic.getPicturePath());
		data.put("describe", topic.getDescribe());
		data.put("browsedNum", topic.getBrowsed());
		data.put("collectedNum", topic.getCollected());
		data.put("createTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(topic.getCreateTime())));
		data.put("works", works);
		User user = userService.queryUser("userID", userID);
		data.put("isCollect", user.getCollectTopic().contains(topicID));
		data.put("workAmount", workAmount);
		data.put("pageAmount", pageAmount);
		return new CommonVO(true, "查询专题成功！", data);
	}
	
	@GetMapping("/topiclist")
	public CommonVO topicList(@RequestParam(name="isRecommend", required=true) Boolean isRecommend,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。", "请先核对后重新输入参数。");
		}
		try {
			Map<String,Object> conditions = new HashMap<String,Object>();
			if (isRecommend) {
				conditions.put("isRecommend", 1);
			}
			List<Topic> topicList = topicService.topicList(conditions, page, size);
			Long topicAmount = topicService.getTopicAmount();
			Long pageAmount = 0l;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Topic topic : topicList) {
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("topicID", topic.getTopicID());
				map.put("topicName", topic.getTopicName());
				map.put("picturePath", topic.getPicturePath());
				map.put("describe", topic.getDescribe());
				List<String> works = topic.getWorks();
				if (works != null && works.size() > 0) {
					for (int i = 0; i < 3 && i < works.size(); i++) {
						String workID = works.get(i);
						Work work = workService.queryWork("workID", workID);
						String picture = null;
						if (work.getPictures() != null && work.getPictures().size() > 0) {
							picture = (String) work.getPictures().get(0).get("filePath");
						}
						map.put("picturePath" + (i+1), picture);
					}
				}
				maplist.add(map);
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("topiclist", maplist);
			if (!isRecommend) {
				data.put("topicAmount", topicAmount);
				data.put("pageAmount", pageAmount);
			}
			return new CommonVO(true, "返回专题列表成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"返回专题列表失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/collectorcancel")
	public SimpleVO collectOrcancel(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="isCollect", required=true) Boolean isCollect,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "用户非法登录。"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			List<String> collectTopic1 = new ArrayList<String>();
			List<String> collectTopic2 = new ArrayList<String>();
			if (user.getCollectTopic() != null) {
				collectTopic1 = user.getCollectTopic();
			}
			
			if (isCollect) {
				if (collectTopic1.contains(topicID)) {
					return new SimpleVO(false, "该专题已被收藏。"); 
				} 
				collectTopic2 = ListUtil.addElement(collectTopic1, topicID);
			} else {
				if (!collectTopic1.contains(topicID)) {
					return new SimpleVO(false, "该专题尚未被收藏。"); 
				} 
				collectTopic2 = ListUtil.removeElement(collectTopic1, topicID);
			}
			userService.setUser("userID", userID, "collectTopic", collectTopic2);
			return new SimpleVO(true, "收藏专题成功！"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
	
}
