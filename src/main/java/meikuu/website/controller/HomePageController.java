package meikuu.website.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import meikuu.domain.entity.user.Admin;
import meikuu.domain.entity.work.Banner;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.ContainUtil;
import meikuu.repertory.service.AdminService;
import meikuu.repertory.service.BannerService;
import meikuu.repertory.service.FileService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.WorkService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

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
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SessionService cmdService;
	
	@GetMapping("/bannerlist")
	public CommonVO bannerList(@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "管理员尚未登录或不存在。", "{}");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 0);
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

	@PostMapping("/editbanner")
	public SimpleVO editBanner(@RequestParam(name="bannerID", required=true) String bannerID,			
			@RequestParam(name="bannerName", required=true) String bannerName,			
			@RequestParam(name="picturePath", required=false) String picturePath,			
			@RequestParam(name="associatedLink", required=true) String associatedLink,			
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "管理员尚未登录或不存在。");
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), 0);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限");
		}
		try {
			Banner banner = bannerService.queryBanner("bannerID", bannerID);
			banner.setBannerName(bannerName);
			if (picturePath != null && !"".equals(picturePath.trim())) {
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
	
	@GetMapping("/workshowlist")
	public CommonVO workShowList(@RequestParam(name="bannerID", required=true) String bannerID,	
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "管理员尚未登录或不存在。", "{}");
		}
		
		int authority = getAuthority(bannerID);
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), authority);
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
					picture = fileService.getFilePath(work.getPictures().get(0));
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

	@PostMapping("/editworkshow")
	public CommonVO editWorkShow(@RequestParam(name="bannerID", required=true) String bannerID,
			@RequestParam(name="index", required=true) Integer index,
			@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new CommonVO(false, "管理员尚未登录或不存在。", "{}");
		}
		
		int primaryClassification = 3;
		int authority = 6;
		switch (Integer.parseInt(bannerID)) {
		case 11:
			authority = 2;
			break;
		case 13:
			primaryClassification = 1;
			authority = 3;
			break;
		case 15:
			primaryClassification = 0;
			authority = 4;
			break;
		case 17:
			primaryClassification = 2;
			authority = 5;
			break;
		}
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), authority);
		if (!flag) {
			return new CommonVO(false, "该用户无此权限。", "请先确认该管理员是否有此权限。");
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new CommonVO(false, "不存在该编号的作品。", "请重新输入正确的作品ID。");
			}
			if (primaryClassification != 3) {
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
	
	@PostMapping("/edithotword")
	public SimpleVO editHotWord(@RequestParam(name="bannerID", required=true) String bannerID1,			
			@RequestParam(name="hotWord", required=true) String[] hotWord1,			
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "管理员尚未登录或不存在。");
		}
		
		int authority = getAuthority(bannerID1);
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限");
		}
		
		try {
			String bannerID2 = String.valueOf(Integer.parseInt(bannerID1)+1);
			List<String> hotWord2 = Arrays.asList(hotWord1);
			bannerService.setBanner("bannerID", bannerID1, "hotWord", hotWord2);
			bannerService.setBanner("bannerID", bannerID2, "hotWord", hotWord2);
			return new SimpleVO(true, "保存热词成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@PostMapping("/removeworkshow")
	public SimpleVO removeWorkShow(@RequestParam(name="bannerID", required=true) String bannerID,
			@RequestParam(name="index", required=true) Integer index,
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "管理员尚未登录或不存在。");
		}
		
		if (index <= 0) {
			return new SimpleVO(false, "输入的参数有误。");
		}
		
		int authority = getAuthority(bannerID);
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), authority);
		if (!flag) {
			return new SimpleVO(false, "该用户无此权限。");
		}
		
		try {
			Banner banner= bannerService.queryBanner("bannerID", bannerID);
			List<String> workShow = banner.getWorkShow();
			workShow.set(index-1, null);
			bannerService.updateBanner(banner);
			return new SimpleVO(true, "删除展示作品成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}

	@PostMapping("/publishworkshow")
	public SimpleVO publishWorkShow(@RequestParam(name="bannerID", required=true) String bannerID1, 
			@RequestParam(name="adminToken", required=true) String sessionId) {
		
		String adminID = cmdService.getIDBySessionId(sessionId);
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (adminID == null || admin == null) {
			return new SimpleVO(false, "管理员尚未登录或不存在。");
		}
	
		int authority = getAuthority(bannerID1);
		
		boolean flag = ContainUtil.hasNumber(admin.getHomepageModule(), authority);
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
				workmap.put("browseNum", work.getBrowseNum()); 
				workmap.put("yijifenlei", work.getYijifenlei());
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
	
	private int getAuthority(String bannerID) {
		int authority = 6;
		switch (Integer.parseInt(bannerID)) {
		case 11:
			authority = 2;
			break;
		case 13:
			authority = 3;
			break;
		case 15:
			authority = 4;
			break;
		case 17:
			authority = 5;
			break;
		}
		return authority;
	}
}
