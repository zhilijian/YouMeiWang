package meikuu.repertory.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import meikuu.domain.entity.work.Banner;
import meikuu.repertory.dao.BannerDao;

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
	
	public void setBanner(String condition, Object value1, String target, Object value2) {
		bannerDao.setBanner(condition, value1, target, value2);
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
