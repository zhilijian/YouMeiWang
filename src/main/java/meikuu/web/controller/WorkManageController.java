package meikuu.web.controller;

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
import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.FileInfo;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.FileService;
import meikuu.repertory.service.NewsService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.repertory.service.VerifyService;
import meikuu.repertory.service.WorkService;
import meikuu.web.vo.CommonVO;
import meikuu.web.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/workmanage")
public class WorkManageController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private WorkService workService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private VerifyService verifyService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private SessionService cmdService;
	
	@PostMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="workIDs", required=true) String workIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "用户尚未登录或不存在。");
		}
		Integer[] workmanage = admin.getWorkManage();
		Integer primaryClassification; 
		Integer authority = 3;
		
		try {
			List<String> workIDlist = Arrays.asList(workIDs.split(","));
			Work work = new Work();
			for (String workID : workIDlist) {
				work = workService.queryWork("workID", workID);
				if (work == null) {
					continue;
				}
				primaryClassification = work.getPrimaryClassification();
				
				switch (primaryClassification) {
				case 0:
					authority = 1;
					break;
				case 1:
					authority = 0;
					break;
				case 2:
					authority = 2;
					break;
				}
				
				boolean flag = ContainUtil.hasNumber(workmanage, authority);
				if (!flag) {
					return new SimpleVO(false, "该用户无此权限。");
				}
				workService.removeWork("workID", workID, "primaryClassification", primaryClassification);
			}
			return new SimpleVO(true, "删除作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/editwork")
	public SimpleVO editWork(@RequestParam(name="workID", required=true) String workID,
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
		@RequestParam(name="remarks", required=false) String remarks,
		@RequestParam(name="pictures", required=false) String[] pictures,
		@RequestParam(name="files", required=false) String[] files,
		@RequestParam(name="isPass", required=false) Integer isPass,
		@RequestParam(name="verifyMessage", required=false) String verifyMessage,
		@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "管理员尚未登录或不存在。");
		}
		
		try {
 			Work work = workService.queryWork("workID", workID);
 			User user = userService.queryUser("userID", work.getAuthor());
 			if (user == null) {
 				return new SimpleVO(false, "该用户不存在。");
			}
 			String userID = user.getUserID();
 			Integer[] workmanage = admin.getWorkManage();
 			Integer authority = 3;
 			
			String[] primaryClassifications = primaryClassification.split(":");
			int primaryClassification0 = Integer.valueOf(primaryClassifications[0]);
			switch (primaryClassification0) {
			case 0:
				authority = 1;
				break;
			case 1:
				authority = 0;
				break;
			case 2:
				authority = 2;
				break;
			}
			boolean flag = ContainUtil.hasNumber(workmanage, authority);
			if (!flag) {
				return new SimpleVO(false, "该管理员无此权限。");
			}
			 
			work.setWorkName(workName);
			work.setPrimaryClassification(primaryClassification0);
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
			work.setRemarks(remarks);
			if (isPass != null) {
				String title = "";
				String content = "";
				switch (isPass) {
				case 0:
					work.setVerifyState(2);
					work.setVerifyMessage(verifyMessage);
					verifyService.verifyNotPassWork(user.getUserID(), workID);
					title = "作品审核未通过。";
					content = "很遗憾，您上传的作品不符合游模网的规则条件。不要气馁，您还可以继续提交申请哦！";
					newsService.addNews(userID, title, content, 1);
					break;
				case 1:
					work.setVerifyState(1);
					verifyService.verifyAndPassWork(user.getUserID(), workID);
					
					Long youbiAmount = user.getYoubiAmount() + 10;
					userService.setUser("userID", userID, "youbiAmount", youbiAmount);
					
					title = "作品审核通过！";
					content = "恭喜你，您上传的作品经游模网审核通过啦！";
					newsService.addNews(userID, title, content, 1);
					break;
				}
			}
			if (pictures != null && pictures.length > 0) {
				List<String> picturelist = Arrays.asList(pictures);
				work.setPictures(picturelist);
			}
			if (files != null && files.length > 0) {
				List<String> filelist = Arrays.asList(files);
				Long modelSize = 0l;
				for (String fileID : filelist) {
					FileInfo fileInfo = fileService.queryFile("fileID", fileID);
					if (fileInfo == null) {
						continue;
					}
					modelSize += fileInfo.getFileSize();
					fileService.setFile("fileID", fileID, "workID", workID);
				}
				work.setFiles(filelist);
				work.setModelSize(modelSize);
			}
			workService.updateWork(work);
			return new SimpleVO(true, "保存模型成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "管理员尚未登录或不存在。", "{}");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			
			int authority = 3;
			int primaryClassification = work.getPrimaryClassification();
			switch (primaryClassification) {
			case 0:
				authority = 1;
				break;
			case 1:
				authority = 0;
				break;
			case 2:
				authority = 2;
				break;
			}
			boolean flag = ContainUtil.hasNumber(admin.getWorkManage(), authority);
			if (!flag) {
				return new CommonVO(false, "该管理员无此权限。", "{}");
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("primaryClassification", work.getPrimaryClassification() + ":" + work.getYijifenlei());
			data.put("secondaryClassification", work.getSecondaryClassification() + ":" + work.getErjifenlei());
			if (work.getReclassify() != null) {
				data.put("reclassify", work.getReclassify() + ":" + work.getSanjifenlei());
			}
			List<String> geshi = new LinkedList<String>();
			for (int i = 0; i < work.getPattern().size(); i++) {
				geshi.add(work.getPattern().get(i) + ":" + work.getGeshi().get(i));
			}
			data.put("pattern", geshi);
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
			return new CommonVO(true, "查看模型详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查看模型详情失败。", "出错信息：" + e.toString());
		}
	}
		
	@GetMapping("/worklist")
	public CommonVO workList(@RequestParam(name="condition", required=false) String condition,
			@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,				
			@RequestParam(name="isVerify", required=true) boolean isVerify,				
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "管理员尚未登录或不存在。", "{}");
		}
		
		int authority = 4;
		if (primaryClassification != null) {
			switch (primaryClassification) {
			case 0:
				authority = 1;
				break;
			case 1:
				authority = 0;
				break;
			case 2:
				authority = 2;
				break;
			}
		}
		
		if (isVerify) {
			authority = 3;
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getWorkManage(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			List<Work> worklist1 = workService.worklist(condition, primaryClassification, isVerify);
			List<Work> worklist2 = new LinkedList<Work>();
			int currIdx = (page > 1 ? (page-1)*size : 0);
			for (int i = 0; i < size && i < worklist1.size()-currIdx; i++) {
				Work work = worklist1.get(currIdx + i);
				worklist2.add(work);
			}
			
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work work : worklist2) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("workID", work.getWorkID());
				map.put("workName", work.getWorkName());
				map.put("author", work.getAuthor());
				map.put("modelSize", work.getModelSize());
				map.put("price", work.getPrice());
				map.put("downloadNum", work.getDownloadNum());
				map.put("primaryClassification", work.getPrimaryClassification());
				map.put("verifyState", work.getVerifyState());
				map.put("isDelete", work.getIsDelete());
				maplist.add(map);
			}
			
			int workAmount = worklist1.size();
			int pageAmount = 0;
			if (workAmount % size == 0) {
				pageAmount = workAmount / size;
			} else {
				pageAmount = workAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("works", maplist);
			data.put("workAmount", workAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询模型成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询模型失败。", "出错信息：" + e.toString());
		}
	}
}
