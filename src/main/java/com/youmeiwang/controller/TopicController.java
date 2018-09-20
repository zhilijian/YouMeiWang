package com.youmeiwang.controller;

import java.util.ArrayList;
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

import com.youmeiwang.entity.Topic;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.FileService;
import com.youmeiwang.service.TopicService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.sessionmanage.SessionService;
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
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SessionService cmdService;
	
	@GetMapping("/topicdetail")
	public CommonVO topicDetail(@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="userToken", required=false) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		
		try {
			Topic topic = topicService.queryTopic("topicID", topicID);
			topicService.setTopic("topicID", topicID, "browsed", topic.getBrowsed() + 1);
			
			List<String> workIDs1 = topic.getWorks();
			List<String> workIDs2 = new LinkedList<String>();
			List<Work> worklist1 = new LinkedList<Work>();
			for (String workID : workIDs1) {
				Work work = workService.queryWork("workID", workID);
				if (work != null) {
					workIDs2.add(workID);
					worklist1.add(work);
				}
			}
			topicService.setTopic("topicID", topicID, "works", workIDs2);
			List<Work> worklist2 = new LinkedList<Work>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < worklist1.size()-currIdx; i++) {
				Work work = worklist1.get(currIdx + i);
				worklist2.add(work);
			}
			
			List<Map<String, Object>> works = new LinkedList<Map<String, Object>>();
			for (Work work : worklist2) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("yijifenlei", work.getYijifenlei());
				workmap.put("price", work.getPrice());
				String picturePath = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picturePath = fileService.getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picturePath);
				workmap.put("collectNum", work.getCollectNum());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("browseNum", work.getBrowseNum());
				if (work.getGeshi() != null && work.getGeshi().size() > 0) {
					workmap.put("pattern", work.getGeshi().get(0)); 
				} else {
					workmap.put("pattern", null); 
				}
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
			data.put("collectedNum", topic.getCollected());
			data.put("createTime", topic.getCreateTime());
			data.put("works", works);
			if (user != null) {
				data.put("isCollect", user.getCollectTopic().contains(topicID));
			}
			data.put("workAmount", workAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询专题详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false,"查询专题详情失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/topiclist")
	public CommonVO topicList(@RequestParam(name="topicType", required=true) Integer topicType,
			@RequestParam(name="page", required=false, defaultValue="1") Integer page,
			@RequestParam(name="size", required=false, defaultValue="4") Integer size,
			@RequestParam(name="userToken", required=false) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		
		if (page <= 0 || size <= 0) {
			return new CommonVO(false, "参数输入不合理。", "请先核对后重新输入参数。");
		}
		try {
			List<Topic> topiclist1 = new ArrayList<Topic>();
			List<Topic> topiclist2 = new ArrayList<Topic>();
			
			switch (topicType) {
			case 1:
				topiclist1 = topicService.topiclist(true);
				break;
			case 2:
				topiclist1 = topicService.topiclist();
				break;
			case 3:
				if (userID == null || user == null) {
					return new CommonVO(false, "用户尚未登录。", "{}"); 
				}
				
				List<String> collectTopic1 = user.getCollectTopic();
				List<String> collectTopic2 = new LinkedList<String>();
				
				for (String topicID : collectTopic1) {
					Topic topic = topicService.queryTopic("topicID", topicID);
					if (topic == null) {
						continue;
					}
					topiclist1.add(topic);
					collectTopic2.add(topicID);
				}
				if (collectTopic2 == null || collectTopic2.size() == 0) {
					return new CommonVO(false, "该用户无收藏专题", "{}");
				}
				userService.setUser("userID", userID, "collectTopic", collectTopic2);
				break;
			}

			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < topiclist1.size()-currIdx; i++) {
				Topic topic = topiclist1.get(currIdx + i);
				topiclist2.add(topic);
			}
			
			Long topicAmount = (long) topiclist1.size();
			Long pageAmount = 0l;
			if (topicAmount % size == 0) {
				pageAmount = topicAmount / size;
			} else {
				pageAmount = topicAmount / size + 1;
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Topic topic : topiclist2) {
				String topicID = topic.getTopicID();
				Map<String, Object> map = new HashMap <String, Object>();
				map.put("topicID", topicID);
				map.put("topicName", topic.getTopicName());
				map.put("picturePath", topic.getPicturePath());
				map.put("describe", topic.getDescribe());
				map.put("collected", topic.getCollected());
				map.put("browsed", topic.getBrowsed());
				
				List<String> workIDs = topic.getWorks();
				if (workIDs != null && workIDs.size() > 0) {
					List<Map<String, Object>> picturePaths = new LinkedList<Map<String, Object>>();
					
					for (int i = 0, j = 0; i < workIDs.size() && j < 3; i++) {
						Map<String, Object> picturemap = new HashMap<String, Object>();
						Work work = workService.queryWork("workID", workIDs.get(i));
						if (work == null) {
							continue;
						}
						String picturePath = null;
						if (work.getPictures() != null && work.getPictures().size() > 0) {
							picturePath = fileService.getFilePath(work.getPictures().get(0));
						}
						picturemap.put("topicID", topicID);
						picturemap.put("picturePath", picturePath);
						picturePaths.add(picturemap);
						j++;
					}
					map.put("picturePaths", picturePaths);
				}
				maplist.add(map);
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("topiclist", maplist);
			if (topicType == 1) {
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
	public SimpleVO collectOrcancel(@RequestParam(name="topicID", required=true) String topicID,
			@RequestParam(name="isCollect", required=true) Boolean isCollect,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "用户尚未登录。"); 
		}
		
		try {
			Topic topic = topicService.queryTopic("topicID", topicID);
			Long collectNum = topic.getCollected();
			
			List<String> collectTopic = new ArrayList<String>();
			if (user.getCollectTopic() != null) {
				collectTopic = user.getCollectTopic();
			}
			
			if (isCollect) {
				if (collectTopic.contains(topicID)) {
					return new SimpleVO(true, "该专题已被收藏。");
				}
				topicService.setTopic("topicID", topicID, "collected", collectNum + 1);
				userService.setUser("userID", userID, "collectTopic", ListUtil.addElement(collectTopic, topicID));
			} else {
				if (!collectTopic.contains(topicID)) {
					return new SimpleVO(true, "该专题尚未被收藏。"); 
				}
				userService.setUser("userID", userID, "collectTopic", ListUtil.removeElement(collectTopic, topicID));
				topicService.setTopic("topicID", topicID, "collected", collectNum - 1);
			}
			return new SimpleVO(true, "收藏/取消专题成功！"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
}
