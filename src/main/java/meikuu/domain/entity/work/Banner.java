package meikuu.domain.entity.work;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meikuu_3dyoo_banner")
public class Banner {

	/**
	 * 标语ID
	 */
	@Indexed
	private String bannerID;
	/**
	 * 标语名称
	 */
	private String bannerName;
	/**
	 * 图片路径
	 */
	private String picturePath;
	/**
	 * 链接
	 */
	private String associatedLink;
	/**
	 * 发布时间
	 */
	private String publishTime;
	/**
	 * 热词
	 */
	private List<String> hotWord;
	/**
	 * 推荐作品
	 */
	private List<String> workShow;
	
	public Banner() {
	}

	public String getBannerID() {
		return bannerID;
	}

	public void setBannerID(String bannerID) {
		this.bannerID = bannerID;
	}

	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getAssociatedLink() {
		return associatedLink;
	}

	public void setAssociatedLink(String associatedLink) {
		this.associatedLink = associatedLink;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public List<String> getHotWord() {
		return hotWord;
	}

	public void setHotWord(List<String> hotWord) {
		this.hotWord = hotWord;
	}

	public List<String> getWorkShow() {
		return workShow;
	}

	public void setWorkShow(List<String> workShow) {
		this.workShow = workShow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bannerID == null) ? 0 : bannerID.hashCode());
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
		Banner other = (Banner) obj;
		if (bannerID == null) {
			if (other.bannerID != null)
				return false;
		} else if (!bannerID.equals(other.bannerID))
			return false;
		return true;
	}
}
