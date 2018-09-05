package com.youmeiwang.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.youmeiwang.entity.Config;
import com.youmeiwang.entity.FileInfo;
import com.youmeiwang.entity.User;
import com.youmeiwang.service.ConfigService;
import com.youmeiwang.service.FileService;
import com.youmeiwang.service.UserService;
import com.youmeiwang.util.FileUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FileService fileService;
	
	@PostMapping(value = "uploadpicture")
    public CommonVO uploadPicture(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="picture", required=true) MultipartFile file,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
        try {
        	Config config1 = configService.queryConfig("configName", "uploadPicturePath");
        	String uploadPath = (String) config1.getValue();
        	Config config2 = configService.queryConfig("configName", "downloadPicture");
        	String downloadPath = (String) config2.getValue();
        	
        	Map<String, Object> configmap = FileUtil.upload(userID, file, uploadPath);
        	String fileName = (String) configmap.get("fileName");
			String filePath = downloadPath + String.valueOf(configmap.get("filePath")).substring(3).replace("\\", "/");
			Long fileSize = (Long) configmap.get("fileSize");
			String pattern = (String) configmap.get("pattern");
			FileInfo fileInfo = fileService.addFile(userID, fileName, filePath, fileSize, pattern);
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
	public CommonVO uploadFile(@RequestParam(name="userID", required=true) String userID,
			@RequestParam(name="file", required=true) MultipartFile file,
			HttpSession session) {
		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
		try {
			Config config = configService.queryConfig("configName", "uploadFilePath");
			String uploadPath = (String) config.getValue();
			
			Map<String, Object> configmap = FileUtil.upload(userID, file, uploadPath);
			String fileName = (String) configmap.get("fileName");
			String filePath = String.valueOf(configmap.get("filePath")).replace("\\", "/");
			Long fileSize = (Long) configmap.get("fileSize");
			String pattern = (String) configmap.get("pattern");
			FileInfo fileInfo = fileService.addFile(userID, fileName, filePath, fileSize, pattern);
			
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
	public SimpleVO download(@RequestParam(name="userID", required=false) String userID,
			@RequestParam(name="adminID", required=false) String adminID,
			@RequestParam(name="fileID", required=true) String fileID,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
//		if (session.getAttribute(userID) == null && session.getAttribute(userID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		try {
			User user = userService.queryUser("userID", userID);
			FileInfo fileInfo = fileService.queryFile("fileID", fileID);
			if (user != null) {
				String workID = fileInfo.getWorkID();
				boolean isPurchase = user.getPurchaseWork().contains(workID);
				if (!isPurchase && !user.getUsername().equals(fileInfo.getAuthor())) {
					return new SimpleVO(false, "该用户尚未购买此作品。");
				}
			}
			String fileName = fileInfo.getFileName();
			String filePath = fileInfo.getFilePath();
			FileUtil.download(userID, fileName, filePath, request, response);
			return new SimpleVO(true, "文件下载成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.toString());
		}
	}
}
