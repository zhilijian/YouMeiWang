package meikuu.repertory.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.work.Work;

/**
 * 作品对象持久层
 * @author zhilijian
 */
@Repository
public interface WorkDao {

	public void addWork(Work work);
	
	public void removeWork(String condition, Object value);

	public void updateWork(Work work);
	
	public void setWork(String condition, Object value1, String target, Object value2);
	
	public Work queryWork(String condition, Object value);
	
	public Long getAmount(String condition, Object value);
	
	public Long getAmount(String condition, Integer primaryClassification, Boolean verifyState);
	
	public Long getAmount(Integer modelType, String condition, Integer primaryClassification, Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType);
	
	public List<Work> workSort(Integer primaryClassification, Integer secondaryClassification, Integer limit);
	
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType, Integer page, Integer size);
	
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState, Integer page, Integer size);
	
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification, String workID);
	
	public List<Work> worklist(Integer primaryClassification, Boolean downloadOrBrowse, Integer page, Integer size);
}
