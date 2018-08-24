package com.youmeiwang.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youmeiwang.dao.BannerDao;
import com.youmeiwang.entity.Banner;

@Service
public class BannerService {

	@Autowired
	private BannerDao bannerDao;
	
	public void addBanner(Banner banner) {
		bannerDao.addBanner(banner);
	}
	
	public void removeBanner(String condition, Object value) {
		bannerDao.removeBanner(condition, value);
	}
	
	public void updateBanner(Banner banner) {
		bannerDao.updateBanner(banner);
	}
	
	public Banner queryBanner(String condition, Object value) {
		return bannerDao.queryBanner(condition, value);
	}
	
	public List<Banner> queryBanner(String condition, Object... values) {
		List<Banner> bannerlist = new LinkedList<Banner>();
		for (Object value : values) {
			bannerlist.add(bannerDao.queryBanner(condition, value));
		}
		return bannerlist;
	}
}
