package meikuu.repertory.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.work.Work;

@Repository
public interface WorkDao {

	public void addWork(Work work);
	
	public void removeWork(String condition, Object value);
	
	public void removeWork(String condition1, Object value1, String condition2, Object value2);

	public void updateWork(Work work);
	
	public void setWork(String condition, Object value1, String target, Object value2);
	
	public Work queryWork(String condition, Object value);
	
	public Long getAmount(String condition, String value);
	
	public Long getAmount(String condition, Object value);
	
	public Long getAmount(String condition1, String value1, String condition2, Object value2);
	
	public Long getAmount(String condition1, Object value1, String condition2, Object value2, String condition3, Object value3);
	
	public Long getAmount(List<Map<String, Object>> conditions);
	
	public Long getAmount(Integer primaryClassification);
	
	public List<Work> workSortDESC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit);

	public List<Work> workSortDESC(String condition, Map<String, Object> conditions, Integer limit);
	
	public List<Work> workSortASC(String condition1, Object value1, String condition2, Object value2, String condition3, Integer limit);
	
	public List<Work> workList(String condition, String value, Integer page, Integer size);
	
	public List<Work> workList(String condition, Integer value, Integer page, Integer size);
	
	public List<Work> workList(Boolean flag, String condition1, String value1, String condition2, Object value2, String condition3, Object value3, Integer page, Integer size);

	public List<Work> workList(String condition1, Object value1, String condition2, Object value2, String condition3, Object value3, Integer page, Integer size);
	
	public List<Work> workList(Integer searchType, String condition, String value, Map<String, Object> conditions, Integer page, Integer size);
	
	public List<Work> workList1(List<Map<String, Object>> conditions, Integer page, Integer size);
	
	public List<Work> worklist(Integer modelType, String condition, Integer primaryClassification, Integer secondaryClassification, Integer reclassify, Integer pattern, Integer sortType);
	
	public List<Work> worklist(String condition, Integer primaryClassification, Boolean verifyState);
	
	public List<Work> worklist(Integer primaryClassification, Integer secondaryClassification);
	
	public List<Work> worklist(Integer primaryClassification, Boolean downloadOrBrowse, Integer page, Integer size);
}
