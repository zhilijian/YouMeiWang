package meikuu.website.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.FileInfo;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.FileUtil;
import meikuu.domain.util.ListUtil;
import meikuu.repertory.service.ConfigService;
import meikuu.repertory.service.FileService;
import meikuu.repertory.service.SessionService;
import meikuu.repertory.service.UserService;
import meikuu.repertory.service.WorkService;
import meikuu.website.vo.CommonVO;
import meikuu.website.vo.SimpleVO;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WorkService workService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SessionService sessionService;
	
	@PostMapping(value = "uploadpicture")
    public CommonVO uploadPicture(@RequestParam(name="picture", required=true) MultipartFile file,
    		@RequestParam(name="token", required=true) String sessionID) {
		
		String ID = sessionService.getIDBySessionId(sessionID);
		if (ID == null) {
			return new CommonVO(false, "用户或管理员尚未登录", "{}");
		}
       
		try {
        	String uploadPath = (String) configService.getConfigValue("uploadPicturePath");
        	String downloadPath = (String) configService.getConfigValue("downloadPicture");
        	
        	Map<String, Object> configmap = FileUtil.upload(ID, file, uploadPath);
        	String fileName = (String) configmap.get("fileName");
			String filePath = downloadPath + String.valueOf(configmap.get("filePath")).substring(40);
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
	public CommonVO uploadFile(@RequestParam(name="file", required=true) MultipartFile file, 
			@RequestParam(name="token", required=true) String sessionID) {
		
		String ID = sessionService.getIDBySessionId(sessionID);
		if (ID == null) {
			return new CommonVO(false, "用户或管理员尚未登录", "{}");
		}
		
		try {
			String uploadPath = (String) configService.getConfigValue("uploadFilePath");
			
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
			@RequestParam(name="token", required=true) String sessionID,
			HttpServletResponse response) {
		
		String ID = sessionService.getIDBySessionId(sessionID);
		if (ID == null) {
			return new SimpleVO(false, "用户或管理员尚未登录");
		}
		User user = userService.queryUser("userID", ID);
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
				return new SimpleVO(false, "该作品并不存在或已被删除。");
			}
			
			FileInfo fileInfo = fileService.queryFile("fileID", fileID);
			if (user != null) {
				boolean isPurchase = user.getPurchaseWork().contains(workID);
				boolean isAuthor = ID.equals(fileInfo.getAuthor());
				if (!isPurchase && !isAuthor) {
					return new SimpleVO(false, "该用户尚未购买此作品。");
				}
				List<String> worklist = ListUtil.addElement(user.getDownWork(), workID);
				userService.setUser("userID", ID, "downWork", worklist);
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
			@RequestParam(name="token", required=true) String sessionID,
			HttpServletResponse response) {
		
		String ID = sessionService.getIDBySessionId(sessionID);
		if (ID == null) {
			return new SimpleVO(false, "用户或管理员尚未登录");
		}
		User user = userService.queryUser("userID", ID);
		
		try {
			Work work = workService.queryWork("workID", workID);
			if (work == null) {
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
				userService.setUser("userID", ID, "downWork", worklist);
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
