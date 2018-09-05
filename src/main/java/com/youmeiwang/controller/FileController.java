package com.youmeiwang.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.youmeiwang.entity.Config;
import com.youmeiwang.service.ConfigService;
import com.youmeiwang.util.FileUtil;
import com.youmeiwang.vo.CommonVO;
import com.youmeiwang.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {
	
	@Autowired
	private ConfigService configService;
	
	@PostMapping(value = "uploadpicture")
    public CommonVO uploadPicture(@RequestParam(name="userID", required=true) String userID,
    					@RequestParam(name="picture", required=true) MultipartFile file,
    					HttpSession session) {
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
		
        try {
        	String uploadPath = getUploadPicturePath();
        	String downloadPath = getdownloadPath();
        	Map<String, Object> data = FileUtil.upload(userID, file, uploadPath);
        	String picturePath = String.valueOf(data.get("filePath")).substring(3).replace("\\", "/");
        	return new CommonVO(true, "图片上传成功！", downloadPath + picturePath);
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
			Map<String, Object> data = FileUtil.upload(userID, file, getUploadFilePath());
			return new CommonVO(true, "文件上传成功！", data);
		} catch (Exception e) {
			e.printStackTrace();
			return new CommonVO(false, "文件上传失败。", "出错原因：" + e.toString());
		}
	}
	
	@PostMapping(value = "download")
	public SimpleVO download(@RequestParam(name="userID", required=true) String userID,
							@RequestParam(name="filePath", required=true) String filePath,
							HttpServletRequest request, HttpServletResponse response, HttpSession session) {
//		if (session.getAttribute(adminID) == null) {
//			return new SimpleVO(false, "该用户尚未登录。");
//		}
		
		try {
			FileUtil.download(userID, filePath, request, response);
			return new SimpleVO(true, "文件下载成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleVO(false, "出错信息：" + e.getMessage());
		}
	}
	
//	@PostMapping(value = "batchupload")
//	public CommonVO batchUpload(@RequestParam(name="userID", required=true) String userID,
//								MultipartHttpServletRequest request) {
//		
//		if (session.getAttribute(adminID) == null) {
//			return new CommonVO(false, "该用户尚未登录。", "请先确认是否登录成功。");
//		}
//		
//		try {
//			Map<String, Object> data = FileUtil.batchUpload(userID, request);
//			return new CommonVO(true, "文件上传成功！", data);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new CommonVO(false, "文件上传失败。", "出错原因：" + e.getMessage());
//		}
//	}
	
	private String getUploadPicturePath() {
		Config config = configService.queryConfig("configName", "uploadPicturePath");
		return (String) config.getValue();
	}
	
	private String getdownloadPath() {
		Config config = configService.queryConfig("configName", "downloadPicture");
		return (String) config.getValue();
	}
	
	private String getUploadFilePath() {
		Config config = configService.queryConfig("configName", "uploadFilePath");
		return (String) config.getValue();
	}
	
}
