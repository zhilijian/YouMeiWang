package meikuu.repertory.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.work.FileInfo;
import meikuu.domain.entity.work.Work;
import meikuu.domain.util.RandomUtil;
import meikuu.repertory.dao.FileDao;
import meikuu.repertory.dao.WorkDao;

/**
 * 作品对象业务层
 * @author zhilijian
 */
@Service
public class WorkService {
	
	@Autowired
	private WorkDao workDao;
	
	@Autowired
	private FileDao fileDao;
	
	/**
	 * 添加作品，并初始化部分属性
	 */
	public Work addWork(String workName, String userID, String primaryClassification, String secondaryClassification, 
			String reclassify, String pattern, Boolean hasTextureMapping, Boolean isBinding, Boolean hasCartoon, Integer price, 
			String[] labels, String remarks, String[] pictures, String[] files) {
		
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
		workDao.addWork(work);
		return work;
	}
	
	/**
	 * 单条件精确移除作品对象
	 */
	public void removeWork(String condition, Object value) {
		workDao.removeWork(condition, value);
	}

	/**
	 * 更新作品（注意mongodb层）
	 */
	public void updateWork(Work work) {
		workDao.updateWork(work);
	}
	
	/**
	 * 单条件精确查询作品对象
	 */
	public Work queryWork(String condition, Object value) {
		return workDao.queryWork(condition, value);
	}
	
	/**
	 * 设置目标作品对象某个属性
	 */
	public void setWork(String condition, Object value1, String target, Object value2) {
		workDao.setWork(condition, value1, target, value2);
	}
	
	/**
	 * 分别查询一级分类的作品对象集合的数量，
	 * 只针对已通过作品，
	 * 用于后台管理项目下载/访问排行接口
	 */
	public Long getAmount(String condition, Object value) {
		return workDao.getAmount(condition, value);
	}
	
	/**
	 * 通过搜索条件、一级分类、是否审核作品查询作品对象集合数量，
	 * 用于后台管理项目作品管理列表/搜索接口
	 */
	public Long getAmount(String condition, Integer primaryClassification, Boolean verifyState) {
		return workDao.getAmount(condition, primaryClassification, verifyState);
	}
	
	/**
	 * 通过作品类型、搜索条件、一级分类、二级分类、三级分类、格式、排序类型查询作品对象集合数量，
	 * 用于前台项目作品搜索接口
	 */
	public Long getAmount(Integer modelType, String condition, Integer primaryClassification,
			Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType) {
		return workDao.getAmount(modelType, condition, primaryClassification, secondaryClassification, reclassify, pattern, sortType);
	}
	
	/**
	 * 根据一级分类和二级分类进行搜索，
	 * 限制展示个数，按照下载量从高到低排序，
	 * 用于作品排序接口
	 */
	public List<Work> workSort(Integer primaryClassification, Integer secondaryClassification, Integer limit) {
		return workDao.workSort(primaryClassification, secondaryClassification, limit);
	}
	
	/**
	 * 通过作品类型、搜索条件、一级分类、二级分类、三级分类、格式、排序类型查询作品对象集合，
	 * 用于前台项目作品搜索接口
	 */
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, 
			Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType, Integer page, Integer size){
		return workDao.worklist(modelType, condition, primaryClassification, secondaryClassification, reclassify, pattern, sortType, page, size);
	}
	
	/**
	 * 通过搜索条件、一级分类、是否审核作品查询作品对象集合，
	 * 用于后台管理项目作品管理列表/搜索接口
	 */
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState, Integer page, Integer size) {
		return workDao.worklist(condition, primaryClassification, verifyState, page, size);
	}
	
	/**
	 * 查询相同一级分类、二级分类的相关作品 ，
	 * workID用于排除作品本身，
	 * 用于前台界面项目作品详情接口 
	 */
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification, String workID) {
		return workDao.worklist(primaryClassification, secondaryClassification, workID);
	}
	
	/**
	 * 通过一级分类查询作品对象集合，
	 * 按下载量或访问量从高到底排序
	 * 用于后台管理项目下载/访问排行接口
	 */
	public List<Work> worklist(Integer primaryClassification, Boolean downloadOrBrowse, Integer page, Integer size) {
		return workDao.worklist(primaryClassification, downloadOrBrowse, page, size);
	}
}
