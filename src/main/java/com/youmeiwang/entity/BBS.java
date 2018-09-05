package com.youmeiwang.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "youmei_bbs")
public class BBS {

	//bbs编号 bbsID
	@Indexed
	private String bbsID;
	//用户ID userID
	private String username;
	//作品ID workID
	private String workID;
	//纠错类型 correctionType
	//0:其他 1：解压出错 2:模型打不开 3:图片与模型不符 4：贴图不完整
	private Integer correctionType;
	//留言内容 comment
	private String comment;
	//类型 type：0：留言反馈 1：纠错提交
	private Integer type;
	//发表时间
	private Long publishTime;
	
	public BBS() {
	}

	public String getBbsID() {
		return bbsID;
	}

	public void setBbsID(String bbsID) {
		this.bbsID = bbsID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}
}
