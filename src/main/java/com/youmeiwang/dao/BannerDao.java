package com.youmeiwang.dao;

import org.springframework.stereotype.Repository;

import com.youmeiwang.entity.Banner;

@Repository
public interface BannerDao {

	public void addBanner(Banner banner);
	
	public void removeBanner(String condition, Object value);
	
	public void updateBanner(Banner banner);
	
	public Banner queryBanner(String condition, Object value);
	
}
