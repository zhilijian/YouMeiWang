package meikuu.website.controller;

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
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

/**
 * 后台管理项目·作品管理
 * @author zhilijian
 */
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
	
	/**
	 * 作品删除
	 */
	@PostMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="workIDs", required=true) String workIDs, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "请求失败，请重新登录。");
		}
		
		
		try {
			List<String> workIDlist = Arrays.asList(workIDs.split(","));
			Work work = new Work();
			Integer[] workmanage = admin.getWorkManage();
			Integer primaryClassification; 
			Integer authority = 10;
			
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
					return new SimpleVO(false, "该管理员无删除作品权限。");
				}
				workService.removeWork("workID", workID);
			}
			return new SimpleVO(true, "删除作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 作品编辑/审核
	 */
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
			return new SimpleVO(false, "请求失败，请重新登录。");
		}
		
		try {
 			Integer[] workmanage = admin.getWorkManage();
 			Integer authority = 10;
 			
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
				return new SimpleVO(false, "该管理员无作品编辑/审核权限。");
			}
			 
			Work work = workService.queryWork("workID", workID);
			String userID = work.getAuthor();
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
			
			if (isPass != null) {
				String title = "";
				String content = "";
				switch (isPass) {
				case 0:
					work.setVerifyState(2);
					work.setVerifyMessage(verifyMessage);
					verifyService.verifyNotPassWork(userID, workID);
					
					title = "作品审核未通过。";
					content = "很遗憾，您上传的作品不符合游模网的规则条件。不要气馁，您还可以继续提交申请哦！";
					newsService.addNews(userID, title, content, 1);
					break;
				case 1:
					work.setVerifyState(1);
					verifyService.verifyAndPassWork(userID, workID);
					
					User user = userService.queryUser("userID", userID);
					userService.setUser("userID", userID, "youbiAmount", user.getYoubiAmount() + 10);
					
					title = "作品审核通过！";
					content = "恭喜你，您上传的作品经游模网审核通过啦！";
					newsService.addNews(userID, title, content, 1);
					break;
				}
			}
			workService.updateWork(work);
			return new SimpleVO(true, "作品编辑/审核成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 作品详情
	 */
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="workID", required=true) String workID, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "请求失败，请重新登录。", "{}");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			
			int authority = 10;
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
				return new CommonVO(false, "该管理员无查看作品详情权限。", "{}");
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
			return new CommonVO(true, "查看作品详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查看作品详情失败。", "出错信息：" + e.toString());
		}
	}
	
	/**
	 * 作品管理列表/搜索
	 */
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
			return new CommonVO(false, "请求失败，请重新登录。", "{}");
		}
		
		int authority = 10;
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
			return new CommonVO(false, "该管理员无作品管理列表/搜索权限。","{}");
		}
		
		try {
			List<Work> worklist = workService.worklist(condition, primaryClassification, isVerify, page, size);
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work work : worklist) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("author", work.getAuthor());
				workmap.put("modelSize", work.getModelSize());
				workmap.put("price", work.getPrice());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("primaryClassification", work.getPrimaryClassification());
				workmap.put("verifyState", work.getVerifyState());
				maplist.add(workmap);
			}
			
			Long workAmount = workService.getAmount(condition, primaryClassification, isVerify);
			Long pageAmount = 0l;
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
	
	/**
	 * 下载/访问排行
	 */
	@PostMapping("/worksort")
	public CommonVO workSort(@RequestParam(name="adminToken", required=true) String sessionId,
			@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,				
			@RequestParam(name="downloadOrBrowse", required=true) boolean downloadOrBrowse,				
			@RequestParam(name="page", required=true) Integer page,
			@RequestParam(name="size", required=true) Integer size) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		
		boolean flag = ContainUtil.hasNumber(admin.getDataStatistics(), 0);
		if (!flag) {
			return new CommonVO(false, "该管理员无此权限。","{}");
		}
		
		try {
			List<Work> worklist = workService.worklist(primaryClassification, downloadOrBrowse, page, size);
			List<Map<String, Object>> maplist = new LinkedList<Map<String, Object>>();
			for (Work work : worklist) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("primaryClassification", work.getPrimaryClassification());
				workmap.put("price", work.getPrice());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("browseNum", work.getBrowseNum());
				maplist.add(workmap);
			}
			
			Long workAmount = workService.getAmount("primaryClassification", primaryClassification);
			Long pageAmount = 0l;
			if (workAmount % size == 0) {
				pageAmount = workAmount / size;
			} else {
				pageAmount = workAmount / size + 1;
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("works", maplist);
			data.put("workAmount", workAmount);
			data.put("pageAmount", pageAmount);
			return new CommonVO(true, "查询下载/访问排行成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询下载/访问排行失败。", "出错信息：" + e.toString());
		}
	}
}
