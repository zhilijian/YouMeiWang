package meikuu.repertory.dao;

import java.util.List;
import java.util.Map;

import meikuu.domain.entity.pay.OrderInfo;

public interface OrderDao {
	
	public void addOrder(OrderInfo order);
	
	public void removeOrder(String condition, Object value);
	
	public void updateOrder(OrderInfo order);
	
	public void setOrder(String condition, Object value1, String target, Object value2);

	public OrderInfo queryOrder(String condition, Object value);
	
	public Long getAmount(String condition, Integer payType, String payStatus, Long startTime, Long endTime);
	
	public List<OrderInfo> orderList(Integer searchType, String condition, String value, Map<String, Object> conditions, Integer page, Integer size);
	
	public List<OrderInfo> orderList(List<Map<String, Object>> conditions, Integer page, Integer size);
	
	public List<OrderInfo> orderlist(String condition, Integer payType, String payStatus, Long startTime, Long endTime, Integer page, Integer size);
}
