package com.youmeiwang.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.youmeiwang.dao.BannerDao;
import com.youmeiwang.entity.Banner;

@Component
public class BannerMongodb implements BannerDao{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addBanner(Banner banner) {
		mongoTemplate.insert(banner);
	}

	@Override
	public void removeBanner(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		mongoTemplate.remove(query, Banner.class);
	}

	@Override
	public void updateBanner(Banner banner) {
		Query query = new Query(Criteria.where("bannerID").is(banner.getBannerID()));
		Update update = new Update();
		update.set("bannerName", banner.getBannerName());
		if (banner.getPicturePath() != null) {
			update.set("picturePath", banner.getPicturePath());
		}
		update.set("associatedLink", banner.getAssociatedLink());
		if (banner.getPublishTime() != null) {
			update.set("publishTime", banner.getPublishTime());
		}
		if (banner.getHotWord() != null) {
			update.set("hotWord", banner.getHotWord());
		}
		if (banner.getWorkShow() != null) {
			update.set("workShow", banner.getWorkShow());
		}
		mongoTemplate.updateFirst(query, update, Banner.class);
	}

	@Override
	public void setBanner(String condition, Object value1, String target, Object value2) {
		Query query = new Query(Criteria.where(condition).is(value1));
		Update update = new Update();
		update.set(target, value2);
		mongoTemplate.updateFirst(query, update, Banner.class);
	}

	@Override
	public Banner queryBanner(String condition, Object value) {
		Query query = new Query(Criteria.where(condition).is(value));
		return mongoTemplate.findOne(query, Banner.class);
	}
}
