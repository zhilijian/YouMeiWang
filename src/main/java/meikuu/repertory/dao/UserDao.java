package meikuu.repertory.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.user.User;

@Repository
public interface UserDao {

	public void addUser(User user);
	
	public void removeUser(String condition, Object value);
	
	public void updateUser(User user);

	public User queryUser(String condition, Object value);
	
	public void setUser(String condition, Object value1, String target, Object value2);
	
	public Long getAmount(String condition, Object value);
	
	public Long getAmount(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify);
	
	public List<User> userlist();
	
	public List<User> userlist(String condition, String value, Integer page, Integer size);
	
	public List<User> userlist(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify, Integer page, Integer size);
}
