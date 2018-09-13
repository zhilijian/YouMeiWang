package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.youmeiwang.entity.Admin;
import com.youmeiwang.entity.Config;
import com.youmeiwang.entity.FileInfo;
import com.youmeiwang.entity.User;
import com.youmeiwang.entity.Work;
import com.youmeiwang.service.AdminService;
import com.youmeiwang.service.ConfigService;
import com.youmeiwang.service.FileService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.service.WorkService;
import com.youmeiwang.util.FileUtil;
import com.youmeiwang.util.ListUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private WorkService workService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FileService fileService;
	
	@PostMapping(value = "uploadpicture")
    public CommonVO uploadPicture(@RequestParam(name="picture", required=true) MultipartFile file,
			HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (user == null && admin == null) {
			return new CommonVO(false, "用户或管理员尚未登录。", "{}") ;
		}
		String ID = userID == null ? adminID : userID;
       
		try {
        	Config config1 = configService.queryConfig("configName", "uploadPicturePath");
        	String uploadPath = (String) config1.getValue();
        	Config config2 = configService.queryConfig("configName", "downloadPicture");
        	String downloadPath = (String) config2.getValue();
        	
        	Map<String, Object> configmap = FileUtil.upload(ID, file, uploadPath);
        	String fileName = (String) configmap.get("fileName");
			String filePath = downloadPath + String.valueOf(configmap.get("filePath")).substring(3).replace("\\", "/");
			Long fileSize = (Long) configmap.get("fileSize");
			String pattern = (String) configmap.get("pattern");
			FileInfo fileInfo = fileService.addFile(ID, fileName, filePath, fileSize, pattern);
			
			Map<String, Object> data = new HashMap<String, Object>();
        	data.put("fileID", fileInfo.getFileID());
        	data.put("filePath", filePath);
        	return new CommonVO(true, "图片上传成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "图片上传失败。", "出错原因：" + e.toString());
		}
    }
	
	@PostMapping(value = "uploadfile")
	public CommonVO uploadFile(@RequestParam(name="file", required=true) MultipartFile file, HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (user == null && admin == null) {
			return new CommonVO(false, "用户或管理员尚未登录。", "{}") ;
		}
		String ID = userID == null ? adminID : userID;
		
		try {
			Config config = configService.queryConfig("configName", "uploadFilePath");
			String uploadPath = (String) config.getValue();
			
			Map<String, Object> configmap = FileUtil.upload(ID, file, uploadPath);
			String fileName = (String) configmap.get("fileName");
			String filePath = String.valueOf(configmap.get("filePath")).replace("\\", "/");
			Long fileSize = (Long) configmap.get("fileSize");
			String pattern = (String) configmap.get("pattern");
			FileInfo fileInfo = fileService.addFile(ID, fileName, filePath, fileSize, pattern);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("fileID", fileInfo.getFileID());
			data.put("fileName", fileName);
			data.put("fileSize", fileSize);
			return new CommonVO(true, "文件上传成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "文件上传失败。", "出错原因：" + e.toString());
		}
	}
	
	@GetMapping(value = "download")
	public SimpleVO download(@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="fileID", required=true) String fileID,
			HttpServletResponse response, HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (user == null && admin == null) {
			return new SimpleVO(false, "用户或管理员尚未登录。") ;
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null || work.getIsDelete()) {
				return new SimpleVO(false, "该作品并不存在或已被删除。");
			}
			
			FileInfo fileInfo = fileService.queryFile("fileID", fileID);
			if (user != null) {
				boolean isPurchase = user.getPurchaseWork().contains(workID);
				boolean isAuthor = user.getUsername().equals(fileInfo.getAuthor());
				if (!isPurchase && !isAuthor) {
					return new SimpleVO(false, "该用户尚未购买此作品。");
				}
				List<String> worklist = ListUtil.addElement(user.getDownWork(), workID);
				userService.setUser("userID", userID, "downWork", worklist);
			}
			
			String fileName = fileInfo.getFileName();
			String filePath = fileInfo.getFilePath();
			FileUtil.download(fileName, filePath, response);
			long downloadNum = work.getDownloadNum() + 1;
			
			workService.setWork("workID", workID, "downloadNum", downloadNum);
			return new SimpleVO(true, "文件下载成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
	
	@GetMapping(value = "downloadZIP")
	public SimpleVO downloadZIP(@RequestParam(name="workID", required=true) String workID,
			@RequestParam(name="fileIDs", required=true) String[] fileIDs,
			HttpServletResponse response, HttpSession session) {
		
		String userID = (String) session.getAttribute("userID");
		User user = userService.queryUser("userID", userID);
		String adminID = (String) session.getAttribute("adminID");
		Admin admin = adminService.queryAdmin("adminID", adminID);
		if (user == null && admin == null) {
			return new SimpleVO(false, "用户或管理员尚未登录。") ;
		}
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null || work.getIsDelete()) {
				return new SimpleVO(false, "该作品并不存在或已被删除。");
			}
			
			List<FileInfo> filelist = new LinkedList<FileInfo>();
			for (int i = 0; i < fileIDs.length; i++) {
				FileInfo fileInfo = fileService.queryFile("fileID", fileIDs[i]);
				if (fileInfo != null) {
					filelist.add(fileInfo);
				}
			}
			if (user != null) {
				String username = user.getUsername();
				boolean isPurchase = user.getPurchaseWork().contains(workID);
				boolean isAuthor = true;
				for (FileInfo fileInfo : filelist) {
					if (!fileInfo.getAuthor().equals(username)) {
						isAuthor = false;
					}
				}
				if (!isPurchase && !isAuthor) {
					return new SimpleVO(false, "该用户并非作者且尚未购买此作品。");
				}
				List<String> worklist = ListUtil.addElement(user.getDownWork(), workID);
				userService.setUser("userID", userID, "downWork", worklist);
			}
			
			FileUtil.downloadZIP(work.getWorkName() + ".zip", filelist, response);
			long downloadNum = work.getDownloadNum() + 1;
			workService.setWork("workID", workID, "downloadNum", downloadNum);
			return new SimpleVO(true, "文件下载成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
}
