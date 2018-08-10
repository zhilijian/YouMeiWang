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
	//authorID 发布者ID（用户ID）
	private String authorID;
	//primaryClassification 一级分类
	//1：共享模型  2：原创模型  3：2D美术
	private Integer primaryClassification;
	//secondaryClassification 二级分类
	private Integer secondaryClassification;
	//reclassify 三级分类
	private Integer reclassify;
	//pattern 格式
	private String pattern;
	//hasTextureMapping 材质贴图
	private boolean hasTextureMapping;
	//isBinding 绑定情况
	private boolean isBinding;
	//hasCartoon 有无动画
	private boolean hasCartoon;
	//price 价格 售价
	private Integer price;
	//currency 币种  1:游币  2：元
	private Integer currency;
	//labels 标签
	private List<String> labels;
	//verifyState 审核状态  1：待审核  2：已通过  3：未通过
	private Integer verifyState;
	//topic 专题
	private String topicID;
	//picturePath 预览图 
	private String[] picturePath;
	//filePath 文件存储路径
	private String filePath;
	//downloadNum 下载次数
	private Long downloadNum;
	
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
	public String getAuthorID() {
		return authorID;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
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
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
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
	public Integer getCurrency() {
		return currency;
	}
	public void setCurrency(Integer currency) {
		this.currency = currency;
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
	public String[] getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String[] picturePath) {
		this.picturePath = picturePath;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Long getDownloadNum() {
		return downloadNum;
	}
	public void setDownloadNum(Long downloadNum) {
		this.downloadNum = downloadNum;
	}
}
