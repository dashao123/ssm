package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;

/**
 * 订单确认页面处理Controller
 * @author s
 *
 */
@Controller
public class OrderCartController {
	
	@Value("${CART_KEY}")
	private String CART_KEY;
	@Autowired
	private OrderService orderService;
	
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		//用户必须是登录状态
		//取用户id
		//根据用户信息取收货列表,使用静态数据
		//把收货地址列表传递到页面
		//从cookie中取购物车商品列表展示到页面
		List<TbItem> cartItemList = getCartItemList(request);
		request.setAttribute("cartList", cartItemList);
		return "order-cart";
	}
	private List<TbItem> getCartItemList(HttpServletRequest request) {
		//从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true);
		if (StringUtils.isBlank(json)) {
			//如果没有内容，返回一个空的列表
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	/**
	 * 创建订单
	 */
	
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,Model model) {
		TaotaoResult result = orderService.createOrder(orderInfo);
		//返回逻辑视图
		model.addAttribute("orderId", result.getData().toString());
		model.addAttribute("payment", orderInfo.getPayment());
		//预计送达时间三天后
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
		return "success";
	}
}
