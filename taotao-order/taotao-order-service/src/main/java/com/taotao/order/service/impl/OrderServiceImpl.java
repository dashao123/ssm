package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
/**
 * 结算商品订单处理
 * @author s
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper shippingMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_BEGIN_VALUE}")
	private String ORDER_ID_BEGIN_VALUE;
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	private String ORDER_ITEM_ID_GEN_KEY;
	
	@Override
	public TaotaoResult createOrder(OrderInfo orderInfo) {
		//生成订单号,可以使用redis的incr生成
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			//设置初始值
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_BEGIN_VALUE);
		}
		//订单号
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		//向订单表中插入数据,需要补全pojo的属性
		orderInfo.setOrderId(orderId);
		//免邮费
		orderInfo.setPostFee("0");
		//设置状态
		orderInfo.setStatus(1);
		//订单时间
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//向订单插入数据
		orderMapper.insert(orderInfo);
		//向订单明细中插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			//获得明细主键
			String oid = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
			tbOrderItem.setId(oid);
			tbOrderItem.setOrderId(orderId);
			//插入明细数据
			orderItemMapper.insert(tbOrderItem);
		}
		//向订单物流插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		shippingMapper.insert(orderShipping);
		//返回订单号
		return TaotaoResult.ok(orderId);
	}

}
