package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

/**
 * 购物车管理Controller
 * @author s
 *
 */
@Controller
public class CartController {
	
	@Value("${CART_KEY}")
	private String CART_KEY;
	@Value("${CART_EXPIRE}")
	private Integer CART_EXPIRE;
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addItemCart(@PathVariable Long itemId,@RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		//取购物车商品列表
		List<TbItem> cartItemList = getCartItemList(request);
		boolean flag = false;
		for (TbItem tbItem : cartItemList) {
			//判断商品是否在购物车中
			if(tbItem.getId() == itemId.longValue()) {
				//如果存在数量相加
				tbItem.setNum(tbItem.getNum()+num);
				flag = true;
				break;
			}
		}
		//不存在,添加一个新的商品
		if(!flag) {
			//需要调用服务,取商品信息
			TbItem item = itemService.getItemById(itemId);
			//设置购买的商品的数量
			item.setNum(num);
			//取一张图片
			String image = item.getImage();
			if(StringUtils.isNotBlank(image)) {
				String[] images = image.split(",");
				item.setImage(images[0]);
				
			}
			//把商品添加到购物车中;
			cartItemList.add(item);
		}
		//吧购物车列表写入cookie中
		CookieUtils.setCookie(request, response, CART_KEY,
				JsonUtils.objectToJson(cartItemList), CART_EXPIRE, true);
		//返回添加成功页面
		return "cartSuccess";
	}
	
	private List<TbItem> getCartItemList(HttpServletRequest request){
		//从cookie中取得购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY,true);
		if(StringUtils.isBlank(json)) {
			//如果没有内容,返回一个空的列表
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
		
	}
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request) {
		//取出购物车的商品
		List<TbItem> cartItemList = getCartItemList(request);
		//添加到request域中
		request.setAttribute("cartList", cartItemList);
		return "cart";
	}
	
	@RequestMapping("/cart/update/num/{itemId}")
	@ResponseBody
	public TaotaoResult updateItemNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		List<TbItem> cartItemList = getCartItemList(request);
		for (TbItem tbItem : cartItemList) {
			//判断商品是否在购物车中
			if(tbItem.getId() == itemId.longValue()) {
				//如果存在数量相加
				tbItem.setNum(num);
				break;
			}
		}
		//吧购物车列表写入cookie中
		CookieUtils.setCookie(request, response, CART_KEY,
				JsonUtils.objectToJson(cartItemList), CART_EXPIRE, true);
		//返回添加成功页面
		return TaotaoResult.ok();
	}
	
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response) {
		//从Cookie中取出购物车
		List<TbItem> cartItemList = getCartItemList(request);
		for (TbItem tbItem : cartItemList) {
			//判断商品是否在购物车中
			if(tbItem.getId() == itemId.longValue()) {
				//删除
				cartItemList.remove(tbItem);
				break;
			}
		}
		//放入Cookie中
		//吧购物车列表写入cookie中
		CookieUtils.setCookie(request, response, CART_KEY,
				JsonUtils.objectToJson(cartItemList), CART_EXPIRE, true);
		//重定向到购物页面
		return "redirect:/cart/cart.html";
	}
}
