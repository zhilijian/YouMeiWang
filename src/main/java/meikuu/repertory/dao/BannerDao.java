package meikuu.repertory.dao;

import org.springframework.stereotype.Repository;

import meikuu.domain.entity.work.Banner;

@Repository
public interface BannerDao {

	public void addBanner(Banner banner);
	
	public void removeBanner(String condition, Object value);
	
	public void updateBanner(Banner banner);
	
	public void setBanner(String condition, Object value1, String target, Object value2);
	
	public Banner queryBanner(String condition, Object value);
	
}
