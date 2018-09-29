package meikuu.repertory.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.user.Admin;

@Repository
public interface AdminDao {

	public void addAdmin(Admin admin);
	
	public void removeAdmin(String condition, Object value);
	
	public void updateAdmin(Admin admin);
	
	public void setAdmin(String condition, Object value1, String target, Object value2);
	
	public Admin queryAdmin(String condition, String value);

	public Admin queryAdmin(String condition1, String value1, String condition2, String value2);
	
	public Long getAmount(String condition, String value);
	
	public List<Admin> adminlist(String condition, String value, Integer page, Integer size);
	
}
