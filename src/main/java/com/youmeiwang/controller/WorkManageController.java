package com.youmeiwang.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.VerifyService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

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
	
	@PostMapping("/removework")
	public SimpleVO removeWork(@RequestParam(name="adminID", required=true) String adminID, 
							@RequestParam(name="workIDs", required=true) String workIDs,
							@RequestParam(name="primaryClassification", required=true) Integer primaryClassification,				
							@RequestParam(name="authority", required=true) Integer authority,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new SimpleVO(false, "该管理员尚未登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			List<String> workIDlist = Arrays.asList(workIDs.split(","));
			for (String workID : workIDlist) {
				workService.removeWork("workID", workID, "primaryClassification", primaryClassification);
			}
			return new SimpleVO(true, "删除作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@PostMapping("/editwork")
	public SimpleVO editWork(@RequestParam(name="adminID", required=true) String adminID, 
		@RequestParam(name="workID", required=true) String workID,
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
		@RequestParam(name="pictures", required=false) String pictures,
		@RequestParam(name="files", required=false) String files,
		@RequestParam(name="authority", required=true) Integer authority,
		@RequestParam(name="isPass", required=false) Integer isPass,
		@RequestParam(name="verifyMessage", required=false) String verifyMessage,
		HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new SimpleVO(false, "该用户尚未登录。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
 			Work work = workService.queryWork("workID", workID);
 			User user = userService.queryUser("username", work.getAuthor());
			if (user == null) {
				return new SimpleVO(false, "该原创用户不存或已注销。");
			}
 			
 			work.setWorkName(workName);
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
			work.setRemarks(remarks);
			if (isPass != null) {
				if (isPass.equals(1)) {
					work.setVerifyState(1);
					verifyService.verifyAndPassWork(user.getUserID(), workID);
				} else {
					work.setVerifyState(2);
					work.setVerifyMessage(verifyMessage);
					verifyService.verifyNotPassWork(user.getUserID(), workID);
				}
			}
			List<Map<String, Object>> picturelist = new ArrayList<Map<String, Object>>();
			if (pictures != null && !"".equals(pictures)) {
				String[] picture = pictures.split(";");
				for (String str : picture) {
					if (str == null) {
						continue;
					}
					@SuppressWarnings("unchecked")
					Map<String, Object> map = new Gson().fromJson(str, Map.class);
					picturelist.add(map);
				}
				work.setPictures(picturelist);
			}
			List<Map<String, Object>> filelist = new ArrayList<Map<String, Object>>();
			if (files != null && !"".equals(files)) {
				String[] file = files.split(";");
				Long modelSize = 0l;
				for (String str : file) {
					if (str == null || str.equals("")) {
						continue;
					}
					@SuppressWarnings("unchecked")
					Map<String, Object> map = new Gson().fromJson(str, Map.class);
					filelist.add(map);
					double d = (double) map.get("fileSize");
					modelSize = modelSize + Math.round(d);
				}
				work.setModelSize(modelSize);
				work.setFiles(filelist);
			}
			workService.updateWork(work);
			return new SimpleVO(true, "保存模型成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
	@GetMapping("/workdetail")
	public CommonVO workDetail(@RequestParam(name="adminID", required=true) String adminID, 
							@RequestParam(name="workID", required=true) String workID,
							@RequestParam(name="authority", required=true) Integer authority,				
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		try {
			Work work = workService.queryWork("workID", workID);
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
			data.put("pictures", work.getPictures());
			data.put("files", work.getFiles());
			return new CommonVO(true, "查看模型详情成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查看模型详情失败。", "出错信息：" + e.getMessage());
		}
	}
		
	@GetMapping("/worklist")
	public CommonVO workList(@RequestParam(name="adminID", required=true) String adminID, 
							@RequestParam(name="condition", required=false) String condition,
							@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,				
							@RequestParam(name="authority", required=true) Integer authority,				
							@RequestParam(name="verifyState", required=false) Integer verifyState,				
							@RequestParam(name="page", required=true) Integer page,
							@RequestParam(name="size", required=true) Integer size,
							HttpSession session) {
		
		if (session.getAttribute(adminID) == null) {
			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getWorkManage(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。","请先申请查看管理员的权限。");
		}
		
		try {
			List<Map<String, Object>> conditions1 = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> conditions2 = new ArrayList<Map<String, Object>>();
			if (condition != null) {
				Map<String, Object> searchCondition1 = new HashMap<String, Object>();
				Map<String, Object> searchCondition2 = new HashMap<String, Object>();
				searchCondition1.put("searchType", 2);
				searchCondition1.put("condition", "workID");
				searchCondition1.put("value", condition);
				conditions1.add(searchCondition1);
				searchCondition2.put("searchType", 2);
				searchCondition2.put("condition", "author");
				searchCondition2.put("value", condition);
				conditions2.add(searchCondition2);
			}
			if (primaryClassification != null) {
				Map<String, Object> searchCondition = new HashMap<String, Object>();
				searchCondition.put("searchType", 1);
				searchCondition.put("condition", "primaryClassification");
				searchCondition.put("value", primaryClassification);
				conditions1.add(searchCondition);
				conditions2.add(searchCondition);
			}
			if (verifyState != null) {
				Map<String, Object> searchCondition = new HashMap<String, Object>();
				searchCondition.put("searchType", 1);
				searchCondition.put("condition", "verifyState");
				searchCondition.put("value", verifyState);
				conditions1.add(searchCondition);
				conditions2.add(searchCondition);
			}
			
			Set<Work> workset = new HashSet<Work>();
			workset.addAll(workService.workList1(conditions1, null, null));
			workset.addAll(workService.workList1(conditions2, null, null));

			List<Work> worklist1 = new ArrayList<Work>(workset);
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
			
			Long workAmount = (long) worklist1.size();
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
			data.put("commissionRate", null);
			return new CommonVO(true, "查询模型成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询模型失败。", "出错信息：" + e.toString());
		}
	}
}
