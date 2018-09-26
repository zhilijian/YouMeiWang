package meikuu.website.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.ListUtil;
import meikuu.repertory.service.ConfigService;
import meikuu.repertory.service.FileService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.repertory.service.VerifyService;
import meikuu.repertory.service.WorkService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

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
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/addverifyingwork") 
	public CommonVO addVerifyingWork(@RequestParam(name="workName", required=true) String workName,
			@RequestParam(name="primaryClassification", required=true) String primaryClassification,
			@RequestParam(name="secondaryClassification", required=true) String secondaryClassification,
			@RequestParam(name="reclassify", required=false) String reclassify,
			@RequestParam(name="pattern", required=true) String pattern,
			@RequestParam(name="hasTextureMapping", required=true) boolean hasTextureMapping,
			@RequestParam(name="isBinding", required=true) boolean isBinding,
			@RequestParam(name="hasCartoon", required=true) boolean hasCartoon,
			@RequestParam(name="price", required=true) Integer price,
			@RequestParam(name="labels", required=true) String[] labels,
			@RequestParam(name="remarks", required=true) String remarks,
			@RequestParam(name="pictures", required=true) String[] pictures,
			@RequestParam(name="files", required=true) String[] files,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录或不存在。", "请先登录再操作"); 
		}
		
		try {
			Work work = workService.addWork(workName, userID, primaryClassification, 
					secondaryClassification, reclassify, pattern, hasTextureMapping, 
					isBinding, hasCartoon, price, labels, remarks, pictures, files);
			String workID = work.getWorkID();
			verifyService.addVerifyingWork(userID, workID);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("nickname", user.getNickname());
			data.put("workID", workID);
			data.put("workname", work.getWorkName());
		
			return new CommonVO(true, "添加作品成功！", data);
		} catch (Exception e) {
			return new CommonVO(false, "添加作品失败。", "错误信息：" + e.toString());
		} 
	}
	
	@GetMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。"); 
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (!user.getUsername().equals(work.getAuthor())) {
				return new SimpleVO(false, "非作者无法删除该作品。"); 
			}
			List<String> worklist = new ArrayList<String>();
			switch (work.getVerifyState()) {
			case 0:
				worklist = ListUtil.removeElement(user.getVerifyingWork(), workID);
				userService.setUser("userID", userID, "verifyingWork", worklist);
				break;
			case 1:
				worklist = ListUtil.removeElement(user.getVerifiedWork(), workID);
				userService.setUser("userID", userID, "verifiedWork", worklist);
				break;
			case 2:
				worklist = ListUtil.removeElement(user.getNotPassWork(), workID);
				userService.setUser("userID", userID, "notPassWork", worklist);
				break;
			default:
				break;
			}
			workService.setWork("workID", workID, "isDelete", true);
			return new SimpleVO(true, "删除作品成功!"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="userToken", required=false) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user1 = userService.queryUser("userID", userID);
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new CommonVO(false, "不存在此ID的作品或者该作品已被作者删除", "{}");
			}
			long browseNum = work.getBrowseNum() + 1;
			workService.setWork("workID", workID, "browseNum", browseNum);
			
			User user2 = userService.queryUser("userID", work.getAuthor());
			String nickname = "";
			String portrait = "";
			if (user2 == null) {
				nickname = "游模网_游客";
				portrait = "";
			} else {
				nickname = user2.getNickname();
				portrait = user2.getPortrait();
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("author", nickname);
			data.put("portrait", portrait);
			data.put("primaryClassification", work.getPrimaryClassification());
			data.put("yijifenlei", work.getYijifenlei());
			data.put("secondaryClassification", work.getSecondaryClassification());
			data.put("erjifenlei", work.getErjifenlei());
			data.put("reclassify", work.getReclassify());
			data.put("sanjifenlei", work.getSanjifenlei());
			data.put("pattern", work.getGeshi());
			data.put("hasTextureMapping", work.isHasTextureMapping());
			data.put("isBinding", work.isBinding());
			data.put("hasCartoon", work.isHasCartoon());
			data.put("price", work.getPrice());
			data.put("labels", work.getLabels());
			data.put("remarks", work.getRemarks());
			List<String> picturelist = new LinkedList<String>();
			for (String fileID : work.getPictures()) {
				String filePath = fileService.getFilePath(fileID);
				if (filePath == null) {
					continue;
				}
				picturelist.add(filePath);
			}
			data.put("pictures", picturelist);
			List<Map<String, Object>> filelist = new LinkedList<Map<String, Object>>();
			for (String fileID : work.getFiles()) {
				Map<String, Object> filemap = fileService.getFileMap(fileID);
				if (filemap == null) {
					continue;
				}
				filelist.add(filemap);
			}
			data.put("files", filelist);
			data.put("browseNum", browseNum);
			data.put("collectNum", work.getCollectNum());
			data.put("downloadNum", work.getDownloadNum());
			data.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(work.getUploadTime())));
			data.put("modelSize", work.getModelSize());
			boolean flag1 = false;
			boolean flag2 = false;
			if (user1 != null) {
				flag1 = user1.getPurchaseWork().contains(workID);
				flag2 = user1.getCollectWork().contains(workID);
			}
			data.put("isPurchase", flag1);
			data.put("isCollected", flag2);
			data.put("discount", configService.getConfigValue("discount"));
			
			List<Work> worklist = workService.worklist(work.getPrimaryClassification(), work.getSecondaryClassification());
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work relatedWork : worklist) {
				Map<String, Object> relatedWorks = new HashMap<String, Object>();
				relatedWorks.put("workID", relatedWork.getWorkID());
				relatedWorks.put("workName", relatedWork.getWorkName());
				String picture = null;
				if (relatedWork.getPictures() != null && relatedWork.getPictures().size() > 0) {
					picture = fileService.getFilePath(relatedWork.getPictures().get(0));
				}
				relatedWorks.put("picture", picture);
				relatedWorks.put("price", relatedWork.getPrice());
				relatedWorks.put("downloadNum", relatedWork.getDownloadNum());
				relatedWorks.put("collectNum", relatedWork.getCollectNum());
				if (relatedWork.getGeshi() != null && relatedWork.getGeshi().size() > 0) {
					relatedWorks.put("pattern", relatedWork.getGeshi().get(0)); 
				} else {
					relatedWorks.put("pattern", null); 
				}
				maplist.add(relatedWorks);
			}
			data.put("maplist", maplist);
			return new CommonVO(true, "查看作品成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查看作品失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/worklist")
	public CommonVO workList(@RequestParam(name="workType", required=true) Integer workType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user = userService.queryUser("userID", userID);
		if (userID == null || user == null) {
			return new CommonVO(false, "用户尚未登录。", "{}"); 
		}
		
		if (workType < 0 || workType > 4) {
			return new CommonVO(false, "作品类型指数输入错误。", "请重新输入正确的作品类型指数。"); 
		}
		
		try {
			List<String> workIDlist1 = new ArrayList<String>();
			switch (workType) {
			case 0:
				workIDlist1 = user.getVerifyingWork();
				break;
			case 1:
				workIDlist1 = user.getVerifiedWork();
				break;
			case 2:
				workIDlist1 = user.getNotPassWork();
				break;
			case 3:
				workIDlist1 = user.getCollectWork();
				break;
			case 4:
				workIDlist1 = user.getDownWork();
				break;
			}
			
			if (workIDlist1 == null || workIDlist1.size() == 0) {
				return new CommonVO(false, "该用户无此类作品。", "{}");
			}
			
			List<String> workIDlist2 = new LinkedList<String>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < workIDlist1.size()-currIdx; i++) {
				String workID = workIDlist1.get(currIdx + i);
				workIDlist2.add(workID);
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (String workID : workIDlist2) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				Work work = workService.queryWork("workID", workID);
				if (work == null) {
					continue;
				}
				workmap.put("workID", workID);
				workmap.put("workName", work.getWorkName());
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = fileService.getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				workmap.put("secondaryClassification", work.getErjifenlei());
				workmap.put("labels", work.getLabels());
				workmap.put("modelSize", work.getModelSize());
				workmap.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(work.getUploadTime())));
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
				workmap.put("browseNum", work.getBrowseNum());
				workmap.put("workType", workType);
				if (work.getGeshi() != null && work.getGeshi().size() > 0) {
					workmap.put("pattern", work.getGeshi().get(0)); 
				} else {
					workmap.put("pattern", null); 
				}
				maplist.add(workmap);
			}
			
			Long workAmount = (long) workIDlist1.size();
			Long pageAmount = 0l;
			if (workAmount % size == 0) {
				pageAmount = workAmount / size;
			} else {
				pageAmount = workAmount / size + 1;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("worklist", maplist);
			data.put("workAmount", workAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "作品展示成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "作品展示失败。", "出错信息：" + e.toString());
		}
	}

	@PostMapping("/worksearch")
	public CommonVO workSearch(@RequestParam(name="modelType", required=false) Integer modelType,
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,
			@RequestParam(name="secondaryClassification", required=false) Integer secondaryClassification,
			@RequestParam(name="reclassify", required=false) Integer reclassify,
			@RequestParam(name="pattern", required=false) Integer pattern,
			@RequestParam(name="sortType", required=false) Integer sortType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		try {
			List<Work> worklist1 = workService.worklist(modelType, condition, primaryClassification, secondaryClassification, reclassify, pattern, sortType);
			List<Work> worklist2 = new LinkedList<Work>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < worklist1.size()-currIdx; i++) {
				Work work = worklist1.get(currIdx + i);
				worklist2.add(work);
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work work : worklist2) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("price", work.getPrice());
				workmap.put("lables", work.getLabels());
				workmap.put("yijifenlei", work.getYijifenlei());
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = fileService.getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
				workmap.put("browseNum", work.getBrowseNum());
				if (work.getGeshi() != null && work.getGeshi().size() > 0) {
					workmap.put("pattern", work.getGeshi().get(0)); 
				} else {
					workmap.put("pattern", null); 
				}
				maplist.add(workmap);
			}
			
			Long workAmount = (long) worklist1.size();
			Long pageAmount = 0l;
			if (workAmount % size == 0) {
				pageAmount = workAmount / size;
			} else {
				pageAmount = workAmount / size + 1;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("worklist", maplist);
			data.put("workAmount", workAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "模型搜索成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "模型搜索失败。", "出错信息：" + e.toString());
		}
	}

	@GetMapping("/worksort")
	public CommonVO workSort(@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,
			@RequestParam(name="secondaryClassification", required=false) Integer secondaryClassification,
			@RequestParam(name="limit", required=true) Integer limit) {
		
		try {
			List<Work> worklist = workService.workSortDESC("primaryClassification", primaryClassification, "secondaryClassification", secondaryClassification, "downloadNum", limit);
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			for (Work work : worklist) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("price", work.getPrice());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum()); 
				workmap.put("browseNum", work.getBrowseNum()); 
				if (work.getGeshi() != null && work.getGeshi().size() > 0) {
					workmap.put("pattern", work.getGeshi().get(0)); 
				} else {
					workmap.put("pattern", null); 
				}
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = fileService.getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				data.add(workmap);
			}
			return new CommonVO(true, "查询模型成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询模型失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/collectorcancel")
	public SimpleVO collectOrCancel(@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="collectOrCancel", required=true) Boolean collectOrCancel,
			@RequestParam(name="userToken", required=true) String sessionId) {
		
		String userID = cmdService.getIDBySessionId(sessionId);
		User user1 = userService.queryUser("userID", userID);
		if (userID == null || user1 == null) {
			return new SimpleVO(false, "用户尚未登录。"); 
		}
		
		try {
 			User user = userService.queryUser("userID", userID);
			Work work = workService.queryWork("workID", workID);
			Long collectNum = work.getCollectNum();
			List<String> collectWork = new ArrayList<String>();
			if (user.getCollectWork() != null) {
				collectWork = user.getCollectWork();
			}
			
			if (collectOrCancel) {
				if (collectWork.contains(workID)) {
					return new SimpleVO(true, "该作品已被收藏。");
				}
				userService.setUser("userID", userID, "collectWork", ListUtil.addElement(collectWork, workID));
				workService.setWork("workID", workID, "collectNum", collectNum + 1);
			} else {
				if (!collectWork.contains(workID)) {
					return new SimpleVO(true, "该作品尚未被收藏。"); 
				} 
				userService.setUser("userID", userID, "collectWork", ListUtil.removeElement(collectWork, workID));
				workService.setWork("workID", workID, "collectNum", collectNum - 1);
			}
			return new SimpleVO(true, "收藏/取消作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
}
