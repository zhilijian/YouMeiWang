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

import com.youmeiwang.entity.Banner;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.BannerService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.ContainUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/homepage")
public class HomePageController {

	@Autowired
	private WorkService workService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private BannerService bannerService;
	
	@GetMapping("/bannerlist")
	public CommonVO bannerList(@RequestParam(name="adminID", required=true) String adminID, 
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认该管理员是否成功登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 0);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先确认该管理员是否有此权限。");
		}
		
		try {
			List<Banner> bannerlist = bannerService.queryBanner("bannerID", "01","02","03","04","05","06","07","08");
			Map<String, Object> data = new HashMap<String, Object>();
			for (Banner banner : bannerlist) {
				Map<String, Object> bannermap = new HashMap<String, Object>();
				bannermap.put("bannerID", banner.getBannerID());
				bannermap.put("bannerName", banner.getBannerName());
				bannermap.put("picturePath", banner.getPicturePath());
				bannermap.put("associatedLink", banner.getAssociatedLink());
				bannermap.put("publishTime", banner.getPublishTime());
				data.put("banner" + banner.getBannerID(), bannermap);
			}
			return new CommonVO(true, "查询banner管理成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(true, "查询banner管理失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/workshowlist")
	public CommonVO workShowList(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID,			
			@RequestParam(name="authority", required=true) Integer authority,			
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认该管理员是否成功登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先确认该管理员是否有此权限。");
		}
		
		try {
			Banner banner = bannerService.queryBanner("bannerID", bannerID);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("bannerID", bannerID);
			for (int i = 0; i < banner.getHotWord().size(); i++) {
				data.put("hotWork"+(i+1), banner.getHotWord().get(i));
			}
			for (int i = 0; i < banner.getWorkShow().size(); i++) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				Work work = workService.queryWork("workID", banner.getWorkShow().get(i));
				if (work == null) {
					workmap.put("workID", null);
					workmap.put("workName", null);
					workmap.put("picturePath", null);
					workmap.put("primaryClassification", null);
					workmap.put("downloadNum", null);
					workmap.put("price", null);
					data.put("workShow" + (i+1), workmap);
					continue;
				}
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = (String) work.getPictures().get(0).get("filePath");
				}
				workmap.put("pictures", picture);
				workmap.put("primaryClassification", work.getYijifenlei());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("price", work.getPrice());
				data.put("workShow" + (i+1), workmap); 
			}
			return new CommonVO(true, "模块展示成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "模块展示失败", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/editbanner")
	public SimpleVO editBanner(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID,			
			@RequestParam(name="bannerName", required=true) String bannerName,			
			@RequestParam(name="picturePath", required=false) String picturePath,			
			@RequestParam(name="associatedLink", required=true) String associatedLink,			
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限");
		}
		try {
			Banner banner = bannerService.queryBanner("bannerID", bannerID);
			banner.setBannerName(bannerName);
			if (picturePath != null) {
				banner.setPicturePath(picturePath);
			}
			banner.setAssociatedLink(associatedLink);
			banner.setPublishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			bannerService.updateBanner(banner);
			return new SimpleVO(true, "修改banner成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "修改banner失败。");
		}
	}
	
	@PostMapping("/edithotword")
	public SimpleVO editHotWord(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID1,			
			@RequestParam(name="hotWord", required=true) String[] hotWord,			
			@RequestParam(name="authority", required=true) Integer authority,			
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限");
		}
		
		try {
			String bannerID2 = String.valueOf(Integer.parseInt(bannerID1)+1);
			Banner banner1 = bannerService.queryBanner("bannerID", bannerID1);
			Banner banner2 = bannerService.queryBanner("bannerID", bannerID2);
			banner1.setHotWord(Arrays.asList(hotWord));
			banner2.setHotWord(Arrays.asList(hotWord));
			bannerService.updateBanner(banner1);
			bannerService.updateBanner(banner2);
			return new SimpleVO(true, "保存热词成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(true, "保存热词成功！");
		}
	}
	
	@PostMapping("/editworkshow")
	public CommonVO editWorkShow(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID,
			@RequestParam(name="index", required=true) Integer index,
			@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="primaryClassification", required=false) Integer primaryClassification,
			@RequestParam(name="authority", required=true) Integer authority,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认该管理员是否成功登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先确认该管理员是否有此权限。");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new CommonVO(false, "不存在该编号的作品。", "请重新输入正确的作品ID。");
			}
			if (primaryClassification != null) {
				if (!work.getPrimaryClassification().equals(primaryClassification)) {
					return new CommonVO(false, "该作品不符合类型要求。", "请重新输入正确的作品ID。");
				}
			}
			Banner banner= bannerService.queryBanner("bannerID", bannerID);
			List<String> workShow = banner.getWorkShow();
			workShow.set(index-1, workID);
			bannerService.updateBanner(banner);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("workID", workID);
			data.put("workName", work.getWorkName());
			data.put("primaryClassification", work.getPrimaryClassification());
			data.put("downloadNum", work.getDownloadNum());
			data.put("price", work.getPrice());
			data.put("pictures", work.getPictures());
			return new CommonVO(true, "展示设置编辑成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "保存展示编辑失败。", "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/removeworkshow")
	public SimpleVO removeWorkShow(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID,
			@RequestParam(name="index", required=true) Integer index,
			@RequestParam(name="authority", required=true) Integer authority,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		if (index <= 0 || authority < 0 || authority > 5) {
			return new SimpleVO(false, "输入的参数有误。");
		}
		
		try {
			Banner banner= bannerService.queryBanner("bannerID", bannerID);
			List<String> workShow = banner.getWorkShow();
			workShow.set(index-1, null);
			bannerService.updateBanner(banner);
			return new SimpleVO(true, "删除展示作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "删除展示作品成功！");
		}
	}
	
	@PostMapping("/publishworkshow")
	public SimpleVO publishWorkShow(@RequestParam(name="adminID", required=true) String adminID, 
			@RequestParam(name="bannerID", required=true) String bannerID1,
			@RequestParam(name="authority", required=true) Integer authority,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
	
		boolean flag = ContainUtil.hasNumber(adminService.queryAdmin("adminID", adminID).getHomepageModule(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			String bannerID2 = String.valueOf(Integer.parseInt(bannerID1)+1);
			Banner banner1 = bannerService.queryBanner("bannerID", bannerID1);
			Banner banner2 = bannerService.queryBanner("bannerID", bannerID2);
			banner2.setWorkShow(banner1.getWorkShow());
			bannerService.updateBanner(banner2);
			return new SimpleVO(true, "发布首页展示成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/getbanner")
	public CommonVO getBanner(@RequestParam(name="type", required=true) Integer type) {
		
		try {
			List<Banner> bannerlist = new ArrayList<Banner>();
			switch (type) {
			case 1:
				bannerlist = bannerService.queryBanner("bannerID", "01","02","03","04","05");
				break;
			case 2:
				bannerlist.add(bannerService.queryBanner("bannerID", "06"));
				break;
			case 3:
				bannerlist.add(bannerService.queryBanner("bannerID", "07"));
				break;
			case 4:
				bannerlist.add(bannerService.queryBanner("bannerID", "08"));
				break;

			default:
				break;
			}
			
			List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
			for (Banner banner : bannerlist) {
				Map<String, Object> bannermap = new HashMap<String, Object>();
				bannermap.put("bannerID", banner.getBannerID());
				bannermap.put("bannerName", banner.getBannerName());
				bannermap.put("picturePath", banner.getPicturePath());
				bannermap.put("associatedLink", banner.getAssociatedLink());
				bannermap.put("publishTime", banner.getPublishTime());
				data.add(bannermap);
			}
			return new CommonVO(true, "查询Banner展示栏成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "查询Banner展示栏失败。", "出错信息：" + e.toString());
		}
	}
	
	@GetMapping("/getrecommend")
	public CommonVO getRecommend(@RequestParam(name="bannerID", required=true) String bannerID) {
		try {
			Banner banner = bannerService.queryBanner("bannerID", bannerID);
			List<Map<String, Object>> worklist = new LinkedList<Map<String, Object>>();
			for (String workID : banner.getWorkShow()) {
				Map<String, Object> workmap = new HashMap<String, Object>();
				Work work = workService.queryWork("workID", workID);
				workmap.put("workID", work.getWorkID());
				workmap.put("workName", work.getWorkName());
				workmap.put("price", work.getPrice());
				workmap.put("downloadNum", work.getDownloadNum());
				workmap.put("collectNum", work.getCollectNum()); 
				workmap.put("yijifenlei", work.getYijifenlei()); 
				String picture = null;
				if (work.getPictures() != null && work.getPictures().size() > 0) {
					picture = (String) work.getPictures().get(0).get("filePath");
				}
				workmap.put("picture", picture);
				worklist.add(workmap);
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("hotword", banner.getHotWord());
			data.put("worklist", worklist);
			return new CommonVO(true, "获取推荐作品成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "获取推荐作品失败。", "出错信息：" + e.toString());
		}
	}
	
	
}
