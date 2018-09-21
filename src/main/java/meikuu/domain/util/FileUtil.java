package meikuu.domain.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import meikuu.domain.entity.work.FileInfo;

public class FileUtil {
	
	public static void downFile(String filename, String path, HttpServletResponse response, HttpServletRequest request, ResourceLoader resourceLoader) {
		
		InputStream inputStream = null;
		ServletOutputStream servletOutputStream = null;
		try {
			org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:" + path);

			response.setContentType("application/vnd.ms-word");
			response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.addHeader("charset", "utf-8");
			response.addHeader("Pragma", "no-cache");
			String encodeName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);

			inputStream = resource.getInputStream();
			servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputStream, servletOutputStream);

			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					servletOutputStream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				// 召唤jvm的垃圾回收器
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String, Object> upload(String userID, MultipartFile file, String fileUrl) throws IllegalStateException, IOException {

		// 获取文件名
        String oldFileName = file.getOriginalFilename();
        // 解决中文问题，liunx下中文路径，图片显示问题
        // 设置文件的前缀名
        String prefixName = generatePrefix();
        // 获取文件的后缀名
        String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
        String newFileName = prefixName + suffixName;
        // 文件上传后的路径
        String filePath = fileUrl + userID + "//";
        
        File destination = new File(filePath + newFileName);
        
        // 检测是否存在目录
        if (!destination.getParentFile().exists()) {
        	destination.getParentFile().mkdirs();
        }
        
        file.transferTo(destination);
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("fileName", oldFileName);
    	data.put("filePath", destination.getAbsolutePath());
    	data.put("fileSize", destination.length());
    	data.put("pattern", suffixName);
    	return data;
	}
	
	public static void download(String fileName, String filePath, HttpServletResponse response) throws UnsupportedEncodingException {
		
		File file = new File(filePath);
		response.setContentType("application/force-download");// 设置强制下载不打开
		response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.addHeader("charset", "utf-8");
		response.addHeader("Pragma", "no-cache");
		String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);
        
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
	}
	
	public static void downloadZIP(String zipName, List<FileInfo> filelist, HttpServletResponse response)
			throws UnsupportedEncodingException {

		response.setContentType("application/force-download");// 设置强制下载不打开
		response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.addHeader("charset", "utf-8");
		response.addHeader("Pragma", "no-cache");
		String encodeName = URLEncoder.encode(zipName, StandardCharsets.UTF_8.toString());
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);

		ZipOutputStream zipos = null;
		DataOutputStream os = null;
		try {
			zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			zipos.setMethod(ZipOutputStream.DEFLATED);// 设置压缩方法

			for (int i = 0; i < filelist.size(); i++) {
				String filePath = filelist.get(i).getFilePath();
				String fileName = filelist.get(i).getFileName();
				File file = new File(filePath);

				// 添加ZipEntry，并ZipEntry中写入文件流
				// 这里，加上i是防止要下载的文件有重名的导致下载失败
				zipos.putNextEntry(new ZipEntry("批量下载" + i + "." + fileName));
				os = new DataOutputStream(zipos);
				InputStream is = new FileInputStream(file);
				byte[] b = new byte[100];
				int length = 0;
				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}
				is.close();
				zipos.closeEntry();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipos != null) {
				try {
					zipos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.gc();
		}
	}
	
	public static String generatePrefix() {
        // 获得当前时间
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换为字符串
        String formatDate = format.format(new Date());
        // 随机生成文件编号
        String random = RandomUtil.getRandomNumber(6);
        return new StringBuffer().append(formatDate).append(random).toString();
    }
}
