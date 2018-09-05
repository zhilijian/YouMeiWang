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

import com.youmeiwang.entity.FileInfo;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.FileService;
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
	
	@Autowired
	private FileService fileService;
	
	@PostMapping("/addverifyingwork") 
	public CommonVO addVerifyingWork(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workName", required=true) String workName,
			@RequestParam(name="primaryClassification", required=true) String primaryClassification,
			@RequestParam(name="secondaryClassification", required=true) String secondaryClassification,
			@RequestParam(name="reclassify", required=false) String reclassify,
			@RequestParam(name="pattern", required=true) String pattern,
			@RequestParam(name="hasTextureMapping", required=true) boolean hasTextureMapping,
			@RequestParam(name="isBinding", required=true) boolean isBinding,
			@RequestParam(name="hasCartoon", required=true) boolean hasCartoon,
			@RequestParam(name="price", required=true) Integer price,
			@RequestParam(name="labels", required=true) String[] labels,
			@RequestParam(name="pictures", required=true) String[] pictures,
			@RequestParam(name="files", required=true) String[] files,
			HttpSession session ) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录再操作"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			String workID = null;
			do {
				workID = RandomUtil.getRandomNumber(10);
			} while (workService.queryWork("workID", workID) != null);
			
			Work work = new Work();
			work.setWorkID(workID);
			work.setWorkName(workName);
			work.setAuthor(user.getUsername());
			String[] primaryClassifications = primaryClassification.split(":");
			work.setPrimaryClassification(Integer.valueOf(primaryClassifications[0]));
			work.setYijifenlei(primaryClassifications[1]);
			String[] secondaryClassifications = secondaryClassification.split(":");
			work.setSecondaryClassification(Integer.valueOf(secondaryClassifications[0]));
			work.setErjifenlei(secondaryClassifications[1]);
			if (reclassify != null) {
				String[] reclassifies = reclassify.split(":");
				work.setReclassify(Integer.valueOf(reclassifies[0]));
				work.setSanjifenlei(reclassifies[1]);
			}
			List<Integer> intlist = new LinkedList<Integer>();
			List<String> strlist = new LinkedList<String>();
			String[] patterns = pattern.split(",");
			for (String str1 : patterns) {
				String[] str2 = str1.split(":");
				intlist.add(Integer.valueOf(str2[0]));
				strlist.add(str2[1]);
			}
			work.setPattern(intlist);
			work.setGeshi(strlist);
			work.setHasTextureMapping(hasTextureMapping);
			work.setBinding(isBinding);
			work.setHasCartoon(hasCartoon);
			work.setPrice(price);
			work.setLabels(Arrays.asList(labels));
			work.setVerifyState(0);
			List<String> picturelist = Arrays.asList(pictures);
			work.setPictures(picturelist);
			List<String> filelist = Arrays.asList(files);
			work.setFiles(filelist);
			Long modelSize = 0l;
			for (String fileID : filelist) {
				FileInfo fileInfo = fileService.queryFile("fileID", fileID);
				if (fileInfo == null) {
					continue;
				}
				modelSize += fileInfo.getFileSize();
			}
			work.setModelSize(modelSize);
			work.setDownloadNum(0l);
			work.setCollectNum(0l);
			work.setBrowseNum(0l);
			work.setUploadTime(System.currentTimeMillis());
			work.setIsDelete(false);
			workService.addWork(work);
			verifyService.addVerifyingWork(userID, workID);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("userID", userID);
			data.put("username", user.getUsername());
			data.put("workID", workID);
			data.put("workname", work.getWorkName());
		
			return new CommonVO(true, "添加作品成功！", data);
		} catch (Exception e) {
			return new CommonVO(false, "添加作品失败。", "错误信息：" + e.toString());
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
			return new SimpleVO(true, "删除作品成功。"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="userID", required=false) String userID,
			@RequestParam(name="workID", required=true) String workID,
			HttpSession session) {
		
		try {
			User user = userService.queryUser("userID", userID);
			Work work = workService.queryWork("workID", workID);
			if (work == null || work.getIsDelete() == true) {
				return new CommonVO(false, "不存在此ID的作品或者该作品已被作者删除", "请查看其他作品。");
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("author", work.getAuthor());
			data.put("primaryClassification", work.getYijifenlei());
			data.put("secondaryClassification", work.getErjifenlei());
			if (work.getReclassify() != null) {
				data.put("reclassify", work.getSanjifenlei());
			}
			data.put("pattern", work.getGeshi());
			data.put("hasTextureMapping", work.isHasTextureMapping());
			data.put("isBinding", work.isBinding());
			data.put("hasCartoon", work.isHasCartoon());
			data.put("price", work.getPrice());
			data.put("labels", work.getLabels());
			if (work.getRemarks() != null) {
				data.put("remarks", work.getRemarks());
			}
			List<String> picturelist = new LinkedList<String>();
			for (String fileID : work.getPictures()) {
				String filePath = getFilePath(fileID);
				if (filePath == null) {
					continue;
				}
				picturelist.add(filePath);
			}
			data.put("pictures", picturelist);
			List<Map<String, Object>> filelist = new LinkedList<Map<String, Object>>();
			for (String fileID : work.getFiles()) {
				Map<String, Object> filemap = getFileMap(fileID);
				if (filemap == null) {
					continue;
				}
				filelist.add(filemap);
			}
			data.put("files", filelist);
			data.put("browseNum", work.getBrowseNum());
			data.put("collectNum", work.getCollectNum());
			data.put("downloadNum", work.getDownloadNum());
			data.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(work.getUploadTime())));
			data.put("modelSize", work.getModelSize());
			if (user != null) {
				boolean flag1 = user.getPurchaseWork().contains(workID);
				boolean flag2 = user.getCollectWork().contains(workID);
				data.put("isPurchase", flag1);
				data.put("isCollected", flag2);
			}
			Map<String, Object> conditions = new HashMap<String, Object>();
			conditions.put("primaryClassification", work.getPrimaryClassification());
			conditions.put("secondaryClassification", work.getSecondaryClassification());
			conditions.put("isDelete", false);
			List<Work> worklist = workService.workSortDESC("downloadNum", conditions, 8);
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work relatedWork : worklist) {
				Map<String, Object> relatedWorks = new HashMap<String, Object>();
				relatedWorks.put("workID", relatedWork.getWorkID());
				relatedWorks.put("workName", relatedWork.getWorkName());
				String picture = null;
				if (relatedWork.getPictures() != null && relatedWork.getPictures().size() > 0) {
					picture = getFilePath(relatedWork.getPictures().get(0));
				}
				relatedWorks.put("picture", picture);
				relatedWorks.put("price", relatedWork.getPrice());
				relatedWorks.put("downloadNum", relatedWork.getDownloadNum());
				relatedWorks.put("collectNum", relatedWork.getCollectNum());
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
	public CommonVO workList(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workType", required=true) Integer workType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录后进行操作");
//		}
		
		if (workType < 0 || workType > 4) {
			return new CommonVO(false, "作品类型指数输入错误。", "请重新输入正确的作品类型指数。"); 
		}
		
		try {
			User user = userService.queryUser("userID", userID);
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
			default:
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
					picture = getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				workmap.put("secondaryClassification", work.getErjifenlei());
				workmap.put("labels", work.getLabels());
				workmap.put("modelSize", work.getModelSize());
				workmap.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(work.getUploadTime())));
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
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

	@PostMapping("/worksearch1")
	public CommonVO workSearch1(@RequestParam(name="modelType", required=false) Integer modelType,
			@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="pattern", required=false) Integer pattern,
			@RequestParam(name="sortType", required=false) Integer sortType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		try {
			List<Work> worklist1 = workService.worklist(modelType, condition, pattern, sortType);;
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
					picture = getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
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
	
	@PostMapping("/worksearch2")
	public CommonVO workSearch2(@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,
			@RequestParam(name="secondaryClassification", required=false) Integer secondaryClassification,
			@RequestParam(name="reclassify", required=false) Integer reclassify,
			@RequestParam(name="pattern", required=false) Integer pattern,
			@RequestParam(name="sortType", required=false) Integer sortType,
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		try {
			List<Work> worklist1 = workService.worklist(primaryClassification, secondaryClassification, reclassify, pattern, sortType);
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
					picture = getFilePath(work.getPictures().get(0));
				}
				workmap.put("picture", picture);
				workmap.put("picture", picture);
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum());
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
			@RequestParam(name="limit", required=true) Integer limit,
			HttpSession session) {
		
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
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = getFilePath(work.getPictures().get(0));
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
	public SimpleVO collectOrCancel(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="collectOrCancel", required=true) Boolean collectOrCancel,
			HttpSession session) {
		
//		if (session.getAttribute(userID) == null) {
//			return new CommonVO(false, "用户非法登录。", "请先登录再操作"); 
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			List<String> collectWork1 = new ArrayList<String>();
			List<String> collectWork2 = new ArrayList<String>();
			if (user.getCollectWork() != null) {
				collectWork1 = user.getCollectWork();
			}
			
			if (collectOrCancel) {
				if (collectWork1.contains(workID)) {
					return new SimpleVO(false, "该作品已被收藏。"); 
				}
				collectWork2 = ListUtil.addElement(collectWork1, workID);
			} else {
				if (!collectWork1.contains(workID)) {
					return new SimpleVO(false, "该作品尚未被收藏。"); 
				} 
				collectWork2 = ListUtil.removeElement(collectWork1, workID);
			}
			userService.setUser("userID", userID, "collectWork", collectWork2);
			return new SimpleVO(true, "收藏/取消作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString()); 
		}
	}
	
	private String getFilePath(String fileID) {
		FileInfo fileInfo = fileService.queryFile("fileID", fileID);
		if (fileInfo == null) {
			return null;
		}
		return fileInfo.getFilePath();
	}
	
	private Map<String, Object> getFileMap(String fileID) {
		FileInfo fileInfo = fileService.queryFile("fileID", fileID);
		if (fileInfo == null) {
			return null;
		}
		Map<String, Object> filemap = new HashMap<String, Object>();
		filemap.put("fileID", fileInfo.getFileID());
		filemap.put("fileName", fileInfo.getFileName());
		filemap.put("fileSize", fileInfo.getFileSize());
		return filemap;
	}
}
