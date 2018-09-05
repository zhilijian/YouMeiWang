package com.youmeiwang.entity;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="youmei_work")
public class Work {

	//workID 用户ID
	@Indexed
	private String workID;
	//workName 作品名称
	private String workName;
	//author 发布者账号（username）
	private String author;
	//primaryClassification 一级分类
	//0：共享模型  1：原创模型  2：2D美术
	private Integer primaryClassification;
	private String yijifenlei;
	//secondaryClassification 二级分类
	private Integer secondaryClassification;
	private String erjifenlei;
	//reclassify 三级分类
	private Integer reclassify; 
	private String sanjifenlei;
	//pattern 格式
	/*
	 * pattern 格式
	 * ma/mb 0，obj 1，3ds 2 ，max 3，fbx 4，ztl 5，
	 * stl 6 unity3d 7，blend 8，lwo 9， poser 10，
	 * skp 11，dae 12，cad 13，通用 14，其他 15
	 */
	private List<Integer> pattern;
	private List<String> geshi;
	//hasTextureMapping 材质贴图
	private boolean hasTextureMapping;
	//isBinding 绑定情况
	private boolean isBinding;
	//hasCartoon 有无动画
	private boolean hasCartoon;
	//price 售价
	private Integer price;
	//labels 标签
	private List<String> labels;
	//verifyState 审核状态  0：待审核  1：已通过  2：未通过
	private Integer verifyState;
	//topic 专题
	private String topicName;
	//picturePath 预览图 
	private List<String> pictures;
	//filePath 文件存储路径
	private List<String> files;
	//downloadNum 被下载次数
	private Long downloadNum;
	//被收藏次数 collectNum
	private Long collectNum;
	//被浏览次数
	private Long browseNum;
	//modelSize 模型大小
	private Long modelSize;
	//remarks 备注 
	private String remarks;
	//审核信息
	private String verifyMessage;
	//上传时间
	private Long uploadTime;
	//
	private Boolean isDelete;
	
	public String getWorkID() {
		return workID;
	}
	public void setWorkID(String workID) {
		this.workID = workID;
	}
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getPrimaryClassification() {
		return primaryClassification;
	}
	public void setPrimaryClassification(Integer primaryClassification) {
		this.primaryClassification = primaryClassification;
	}
	public String getYijifenlei() {
		return yijifenlei;
	}
	public void setYijifenlei(String yijifenlei) {
		this.yijifenlei = yijifenlei;
	}
	public Integer getSecondaryClassification() {
		return secondaryClassification;
	}
	public void setSecondaryClassification(Integer secondaryClassification) {
		this.secondaryClassification = secondaryClassification;
	}
	public String getErjifenlei() {
		return erjifenlei;
	}
	public void setErjifenlei(String erjifenlei) {
		this.erjifenlei = erjifenlei;
	}
	public Integer getReclassify() {
		return reclassify;
	}
	public void setReclassify(Integer reclassify) {
		this.reclassify = reclassify;
	}
	public String getSanjifenlei() {
		return sanjifenlei;
	}
	public void setSanjifenlei(String sanjifenlei) {
		this.sanjifenlei = sanjifenlei;
	}
	public List<Integer> getPattern() {
		return pattern;
	}
	public void setPattern(List<Integer> pattern) {
		this.pattern = pattern;
	}
	public List<String> getGeshi() {
		return geshi;
	}
	public void setGeshi(List<String> geshi) {
		this.geshi = geshi;
	}
	public boolean isHasTextureMapping() {
		return hasTextureMapping;
	}
	public void setHasTextureMapping(boolean hasTextureMapping) {
		this.hasTextureMapping = hasTextureMapping;
	}
	public boolean isBinding() {
		return isBinding;
	}
	public void setBinding(boolean isBinding) {
		this.isBinding = isBinding;
	}
	public boolean isHasCartoon() {
		return hasCartoon;
	}
	public void setHasCartoon(boolean hasCartoon) {
		this.hasCartoon = hasCartoon;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public Integer getVerifyState() {
		return verifyState;
	}
	public void setVerifyState(Integer verifyState) {
		this.verifyState = verifyState;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public List<String> getPictures() {
		return pictures;
	}
	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
	public Long getDownloadNum() {
		return downloadNum;
	}
	public void setDownloadNum(Long downloadNum) {
		this.downloadNum = downloadNum;
	}
	public Long getCollectNum() {
		return collectNum;
	}
	public void setCollectNum(Long collectNum) {
		this.collectNum = collectNum;
	}
	public Long getBrowseNum() {
		return browseNum;
	}
	public void setBrowseNum(Long browseNum) {
		this.browseNum = browseNum;
	}
	public Long getModelSize() {
		return modelSize;
	}
	public void setModelSize(Long modelSize) {
		this.modelSize = modelSize;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getVerifyMessage() {
		return verifyMessage;
	}
	public void setVerifyMessage(String verifyMessage) {
		this.verifyMessage = verifyMessage;
	}
	public Long getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Long uploadTime) {
		this.uploadTime = uploadTime;
	}
	public Boolean getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((workID == null) ? 0 : workID.hashCode());
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
		Work other = (Work) obj;
		if (workID == null) {
			if (other.workID != null)
				return false;
		} else if (!workID.equals(other.workID))
			return false;
		return true;
	}
}
