package com.youmeiwang.entity;

import java.util.List;
import java.util.Map;

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
	//secondaryClassification 二级分类
	private Integer secondaryClassification;
	//reclassify 三级分类
	private Integer reclassify;
	//pattern 格式
	private List<Integer> pattern;
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
	private String topicID;
	//picturePath 预览图 
	private List<String> picturePath;
	//filePath 文件存储路径
	private List<Map<String, String>> fileNameAndPath;
	//downloadNum 下载次数
	private Long downloadNum;
	//被收藏次数 collectNum
	private Long collectNum;
	//modelSize 模型大小
	private double modelSize;
	//remarks 备注 
	private String remarks;
	//审核信息
	private String verifyMessage;
	//上传时间
	private String uploadTime;
	
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
	public Integer getSecondaryClassification() {
		return secondaryClassification;
	}
	public void setSecondaryClassification(Integer secondaryClassification) {
		this.secondaryClassification = secondaryClassification;
	}
	public Integer getReclassify() {
		return reclassify;
	}
	public void setReclassify(Integer reclassify) {
		this.reclassify = reclassify;
	}
	public List<Integer> getPattern() {
		return pattern;
	}
	public void setPattern(List<Integer> pattern) {
		this.pattern = pattern;
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
	public Work() {
	}
	public String getTopicID() {
		return topicID;
	}
	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}
	public List<String> getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(List<String> picturePath) {
		this.picturePath = picturePath;
	}
	public List<Map<String, String>> getFileNameAndPath() {
		return fileNameAndPath;
	}
	public void setFileNameAndPath(List<Map<String, String>> fileNameAndPath) {
		this.fileNameAndPath = fileNameAndPath;
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
	public double getModelSize() {
		return modelSize;
	}
	public void setModelSize(double modelSize) {
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
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
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
