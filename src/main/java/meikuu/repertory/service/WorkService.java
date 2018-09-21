package meikuu.repertory.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.user.User;
import meikuu.domain.entity.work.FileInfo;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.ListUtil;
import meikuu.domain.util.RandomUtil;
import meikuu.repertory.dao.FileDao;
import meikuu.repertory.dao.UserDao;
import meikuu.repertory.dao.WorkDao;

@Service
public class WorkService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private FileDao fileDao;
	
	public Work addWork(String workName, String userID, String primaryClassification, 
			String secondaryClassification, String reclassify, String pattern, boolean hasTextureMapping, 
			boolean isBinding, boolean hasCartoon, Integer price, String[] labels, String remarks, 
			String[] pictures, String[] files) {
		
		Work work = new Work();
		String workID = null;
		do {
			workID = RandomUtil.getRandomNumber(10);
		} while (workDao.queryWork("workID", workID) != null);
		
		work.setWorkID(workID);
		work.setWorkName(workName);
		work.setAuthor(userID);
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
		work.setVerifyState(0);
		List<String> picturelist = Arrays.asList(pictures);
		work.setPictures(picturelist);
		List<String> filelist = Arrays.asList(files);
		work.setFiles(filelist);
		Long modelSize = 0l;
		for (String fileID : filelist) {
			FileInfo fileInfo = fileDao.queryFile("fileID", fileID);
			if (fileInfo == null) {
				continue;
			}
			modelSize += fileInfo.getFileSize();
		}
		work.setModelSize(modelSize);
		work.setDownloadNum(0l);
		work.setCollectNum(0l);
		work.setBrowseNum(0l);
		work.setUploadTime(System.currentTimeMillis());
		work.setIsDelete(false);
		workDao.addWork(work);
		return work;
	}
	
	public void addCollectWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> collectWork = ListUtil.addElement(user.getCollectWork(), workID);
		user.setVerifyingWork(collectWork);
		userDao.updateUser(user);
	}
	
	public void removeWork(String condition, Object value) {
		workDao.removeWork(condition, value);
	}
	
	public void removeWork(String condition1, Object value1, String condition2, Object value2) {
		workDao.removeWork(condition1, value1, condition2, value2);
	}
	
	public void removeCollectWork(String userID, String workID) {
		User user = userDao.queryUser("userID", userID);
		List<String> collectWork = ListUtil.removeElement(user.getCollectWork(), workID);
		user.setVerifyingWork(collectWork);
		userDao.updateUser(user);
	}

	public void updateWork(Work work) {
		workDao.updateWork(work);
	}
	
	public Work queryWork(String condition, Object value) {
		return workDao.queryWork(condition, value);
	}
	
	public void setWork(String condition, Object value1, String target, Object value2) {
		workDao.setWork(condition, value1, target, value2);
	}
	
	public String queryWorkName(String condition, Object value) {
		Work work = workDao.queryWork(condition, value);
		return work.getWorkName();
	}
	
	public Long getAmount(String condition, String value) {
		return workDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition, Object value) {
		return workDao.getAmount(condition, value);
	}
	
	public Long getAmount(String condition1, String value1, String condition2, Object value2) {
		return workDao.getAmount(condition1, value1, condition2, value2);
	}
	
	public Long getAmount(String condition1, Object value1, String condition2, Object value2, String condition3, Object value3) {
		return workDao.getAmount(condition1, value1, condition2, value2, condition3, value3);
	}
	
	public Long getAmount(List<Map<String, Object>> conditions) {
		return workDao.getAmount(conditions);
	}
	
	public List<Work> workSortDESC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		return workDao.workSortDESC(condition1, value1, condition2, value2, condition3, limit);
	}
	
	public List<Work> workSortASC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit) {
		return workDao.workSortASC(condition1, value1, condition2, value2, condition3, limit);
	}
	
	public List<Work> workSortDESC(String condition, Map<String, Object> conditions, Integer limit) {
		return workDao.workSortDESC(condition, conditions, limit);
	}
	
	public List<Work> workList(String condition, List<String> values) {
		List<Work> worklist = new LinkedList<Work>();
		for (String value : values) {
			worklist.add(workDao.queryWork(condition, value));
		}
		return worklist;
	}
	
	public List<Work> workList(String condition, String value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size) {
		return workDao.workList(condition, value, page, size);
	}
	
	public List<Work> workList(Boolean flag, String condition1, String value1, 
			String condition2, Integer value2, String condition3, Integer value3, Integer page, Integer size) {
		return workDao.workList(flag, condition1, value1, condition2, value2, condition3, value3, page, size);
	}
	
	public List<Work> workList(String condition1, Object value1, 
			String condition2, Object value2, String condition3, Object value3, Integer page, Integer size) {
		return workDao.workList(condition1, value1, condition2, value2, condition3, value3, page, size);
	}
	
	public List<Work> workList(Integer searchTpye, String condition, String value, Map<String, Object> conditions, Integer page, Integer size) {
		return workDao.workList(searchTpye, condition, value, conditions, page, size);
	}
	
	public List<Work> workList1(List<Map<String, Object>> conditions, Integer page, Integer size) {
		return workDao.workList1(conditions, page, size);
	}
	
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, 
			Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType){
		return workDao.worklist(modelType, condition, primaryClassification, secondaryClassification, reclassify, pattern, sortType);
	}
	
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState) {
		return workDao.worklist(condition, primaryClassification, verifyState);
	}
	
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification) {
		return workDao.worklist(primaryClassification, secondaryClassification);
	}
}
