package meikuu.repertory.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.user.User;
import meikuu.domain.util.RandomUtil;
import meikuu.repertory.dao.UserDao;

/**
 * 用户对象业务层
 * @author zhilijian
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ConfigService configService;

	/**
	 * 添加用户对象，并初始化部分属性
	 */
	public User addUser(String username) {
		User user = new User();
		String userID;
		do {
			userID = RandomUtil.getRandomNumber(8);
		} while (userDao.queryUser("userID", userID) != null);
		user.setUserID(userID);
		user.setUsername(username);
		user.setNickname("游模网_游客");
		user.setSex(0);
		user.setYoubiAmount(0l);
		user.setBalance(0.0);
		Set<Integer> vipKind = new HashSet<Integer>();
		vipKind.add(0);
		user.setVipKind(vipKind);
		user.setPortrait((String)configService.getConfigValue("portrait"));
		user.setMemberKind(0);
		user.setVerifyingWork(new ArrayList<String>());
		user.setVerifiedWork(new ArrayList<String>());
		user.setNotPassWork(new ArrayList<String>());
		user.setCollectWork(new ArrayList<String>());
		user.setCollectTopic(new ArrayList<String>());
		user.setDownWork(new ArrayList<String>());
		user.setPurchaseWork(new ArrayList<String>());
		user.setApplyForOriginal(0);
		userDao.addUser(user);
		return user;
	}
	
	/**
	 * 单条件精确移除用户对象
	 */
	public void removeUser(String condition, String value) {
		userDao.removeUser(condition, value);
	}
	
	/**
	 * 更新用户对象（注意mongodb层）
	 */
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	/**
	 * 单条件查询用户对象
	 */
	public User queryUser(String condition, Object value) {
		return userDao.queryUser(condition, value);
	}
	
	/**
	 * 设置目标对象的的属性性
	 */
	public void setUser(String condition, Object value1, String target, Object value2) {
		userDao.setUser(condition, value1, target, value2);
	}
	
	/**
	 * 查询用户昵称
	 */
	public String queryNickname(String condition, Object value) {
		User user = userDao.queryUser(condition, value);
		return user.getNickname();
	}
	
	/**
	 * 单条件精确查询用户对象集合数目
	 */
	public Long getAmount(String condition, Object value) {
		return userDao.getAmount(condition, value);
	}
	
	/**
	 * 通过搜索条件、VIP类别、身份（会员类别）查选用户对象集合数量
	 * 用于普通用户/原创作者 查询/搜索接口
	 */
	public Long getAmount(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify) {
		return userDao.getAmount(condition, VIPKind, memberKind, isVerify);
	}
	
	/**
	 * 查询大众用户以外的会员用户对象集合，
	 * 用于定时器Controller
	 */
	public List<User> userlist() {
		return userDao.userlist();
	}
	
	/**
	 * 单条件模糊分页查询用户对象集合
	 */
	public List<User> userlist(String condition, String value, Integer page, Integer size) {
		return userDao.userlist(condition, value, page, size);
	}
	
	/**
	 * 通过搜索条件、VIP类别、身份（会员类别）查选用户对象集合
	 * 用于普通用户/原创作者 查询/搜索接口
	 */
	public List<User> userlist(String condition, Integer VIPKind, Integer memberKind, Boolean isVerify, Integer page, Integer size) {
		return userDao.userlist(condition, VIPKind, memberKind, isVerify, page, size);
	}
}
