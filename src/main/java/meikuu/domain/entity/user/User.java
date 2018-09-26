package meikuu.domain.entity.user;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_user")
public class User {

	/**
	 * 用户ID
	 */
	@Indexed
	private String userID;
	/**
	 * 用户统一标识。
	 * 针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
	 */
	private String unionid;
	/**
	 * 用户账号
	 */
	private String username;
	/**
	 * 用户昵称
	 */
	private String nickname;
	/**
	 * 真实姓名
	 */
	private String fullname;
	/**
	 * 性别
	 * 0:未知  1：男  2：女
	 */
	private Integer sex;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 用户头像(文件路径)
	 */
	private String portrait;
	/**
	 * 支付宝
	 */
	private String alipay;
	/**
	 * QQ账号
	 */
	private String qq;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 游币数量
	 */
	private Long youbiAmount;
	/**
	 * 余额
	 */
	private Double balance;
	/**
	 * VIP类别
	 * 0：大众用户 1：共享VIP 2：原创VIP 3：企业VIP
	 */
	private Set<Integer> vipKind;
	/**
	 * 共享VIP免费下载次数
	 */
	private Integer freedownload;
	/**
	 * 共享VIP到期时间
	 */
	private Long shareVIPTime;
	/**
	 * 原创VIP到期时间
	 */
	private Long originalVIPTime;
	/**
	 * 企业VIP到期时间
	 */
	private Long companyVIPTime;
	/**
	 * 会员种类
	 * 0：普通用户 1：原创用户
	 */
	private Integer memberKind;
	/**
	 * 待审核作品
	 */
	private List<String> verifyingWork;
	/**
	 * 已通过作品
	 */
	private List<String> verifiedWork;
	/**
	 * 未通过作品
	 */
	private List<String> notPassWork;
	/**
	 * 收藏的作品
	 */
	private List<String> collectWork;
	/**
	 * 收藏的专题
	 */
	private List<String> collectTopic;
	/**
	 * 下载的作品
	 */
	private List<String> downWork;
	/**
	 * 购买的作品
	 */
	private List<String> purchaseWork;
	/**
	 * 申请原创作者
	 * 0：未申请 1：申请中 2：申请成功 3：申请驳回
	 */
	private Integer applyForOriginal;
	/**
	 * 审核信息
	 */
	private String verifyMessage;
	/**
	 * 禁止登录
	 */
	private boolean isBanLogin;

	public User() {
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getYoubiAmount() {
		return youbiAmount;
	}

	public void setYoubiAmount(Long youbiAmount) {
		this.youbiAmount = youbiAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Set<Integer> getVipKind() {
		return vipKind;
	}

	public void setVipKind(Set<Integer> vipKind) {
		this.vipKind = vipKind;
	}

	public Integer getFreedownload() {
		return freedownload;
	}

	public void setFreedownload(Integer freedownload) {
		this.freedownload = freedownload;
	}

	public Long getShareVIPTime() {
		return shareVIPTime;
	}

	public void setShareVIPTime(Long shareVIPTime) {
		this.shareVIPTime = shareVIPTime;
	}

	public Long getOriginalVIPTime() {
		return originalVIPTime;
	}

	public void setOriginalVIPTime(Long originalVIPTime) {
		this.originalVIPTime = originalVIPTime;
	}

	public Long getCompanyVIPTime() {
		return companyVIPTime;
	}

	public void setCompanyVIPTime(Long companyVIPTime) {
		this.companyVIPTime = companyVIPTime;
	}

	public Integer getMemberKind() {
		return memberKind;
	}

	public void setMemberKind(Integer memberKind) {
		this.memberKind = memberKind;
	}

	public List<String> getVerifyingWork() {
		return verifyingWork;
	}

	public void setVerifyingWork(List<String> verifyingWork) {
		this.verifyingWork = verifyingWork;
	}

	public List<String> getVerifiedWork() {
		return verifiedWork;
	}

	public void setVerifiedWork(List<String> verifiedWork) {
		this.verifiedWork = verifiedWork;
	}

	public List<String> getNotPassWork() {
		return notPassWork;
	}

	public void setNotPassWork(List<String> notPassWork) {
		this.notPassWork = notPassWork;
	}

	public List<String> getCollectWork() {
		return collectWork;
	}

	public void setCollectWork(List<String> collectWork) {
		this.collectWork = collectWork;
	}

	public List<String> getCollectTopic() {
		return collectTopic;
	}

	public void setCollectTopic(List<String> collectTopic) {
		this.collectTopic = collectTopic;
	}

	public List<String> getDownWork() {
		return downWork;
	}

	public void setDownWork(List<String> downWork) {
		this.downWork = downWork;
	}

	public List<String> getPurchaseWork() {
		return purchaseWork;
	}

	public void setPurchaseWork(List<String> purchaseWork) {
		this.purchaseWork = purchaseWork;
	}

	public Integer getApplyForOriginal() {
		return applyForOriginal;
	}

	public void setApplyForOriginal(Integer applyForOriginal) {
		this.applyForOriginal = applyForOriginal;
	}

	public String getVerifyMessage() {
		return verifyMessage;
	}

	public void setVerifyMessage(String verifyMessage) {
		this.verifyMessage = verifyMessage;
	}

	public boolean isBanLogin() {
		return isBanLogin;
	}

	public void setBanLogin(boolean isBanLogin) {
		this.isBanLogin = isBanLogin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}
}
