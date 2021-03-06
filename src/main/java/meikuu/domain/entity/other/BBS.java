package meikuu.domain.entity.other;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_bbs")
public class BBS {

	/**
	 * bbs编号
	 */
	@Indexed
	private String bbsID;
	/**
	 * 用户ID
	 */
	private String userID;
	/**
	 * 作品ID
	 */
	private String workID;
	/**
	 * 纠错类型
	 * 0:其他 1：解压出错 2:模型打不开 3:图片与模型不符 4：贴图不完整
	 */
	private Integer correctionType;
	/**
	 * 留言内容
	 */
	private String comment;
	/**
	 * 类型 type
	 * 0：留言反馈 1：纠错提交
	 */
	private Integer type;
	/**
	 * 发表时间
	 */
	private String publishTime;
	
	public BBS() {
	}

	public String getBbsID() {
		return bbsID;
	}

	public void setBbsID(String bbsID) {
		this.bbsID = bbsID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getWorkID() {
		return workID;
	}

	public void setWorkID(String workID) {
		this.workID = workID;
	}

	public Integer getCorrectionType() {
		return correctionType;
	}

	public void setCorrectionType(Integer correctionType) {
		this.correctionType = correctionType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
}
