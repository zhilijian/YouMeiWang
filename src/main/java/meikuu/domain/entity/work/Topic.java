package meikuu.domain.entity.work;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="meikuu_3dyoo_topic")
public class Topic {
	
	/**
	 * 专题编号
	 */
	@Indexed
	private String topicID;
	/**
	 * 专题标题
	 */
	private String topicName;
	/**
	 * 主图路径
	 */
	private String picturePath;
	/**
	 * 专题描述
	 */
	private String describe;
	/**
	 * 是否推荐
	 */
	private Boolean isRecommend;
	/**
	 * 创建日期
	 */
	private String createTime;
	/**
	 * 被浏览数目
	 */
	private Long browsed;
	/**
	 * 被收藏数目
	 */
	private Long collected;
	/**
	 * 关联模型（作品编号）
	 */
	private List<String> works;
	
	public Topic() {}

	public String getTopicID() {
		return topicID;
	}

	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getDescribe() {
		return describe;
	}

	public Boolean getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Long getBrowsed() {
		return browsed;
	}

	public void setBrowsed(Long browsed) {
		this.browsed = browsed;
	}

	public Long getCollected() {
		return collected;
	}

	public void setCollected(Long collected) {
		this.collected = collected;
	}

	public List<String> getWorks() {
		return works;
	}

	public void setWorks(List<String> works) {
		this.works = works;
	}
}
