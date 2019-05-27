package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
/**
 * 判断用户是否登录拦截器
 * @author s
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
	
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//执行handler之前先执行此方法
		//1.从cookie总取出token信息
		String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
		//2.如果取不到token,跳转到sso的登录页面,需要把当前请求的url作为参数传递给sso系统
		if(StringUtils.isBlank(token)) {
			//取得当前请求的url,传递给sso登录页面
			String url = request.getRequestURL().toString();
			//跳转到登录页面sso
			response.sendRedirect(SSO_URL+"/page/login?url:"+url);
			//拦截
			return false;
		}
		//3.取到token,调用sso系统的服务判断用户是否登录
		TaotaoResult taotaoResult = userService.getUserByToken(token);
		//4.如果用户未登录,即没有取到用户信息,跳转到sso的登录页面
		if(taotaoResult.getStatus() != 200) {
			//取得当前请求的url,传递给sso登录页面
			String url = request.getRequestURL().toString();
			//跳转到登录页面sso
			response.sendRedirect(SSO_URL+"/page/login?url:"+url);
			return false;
		}
		//5.如果取到用户信息,放行
		//吧用户信息放到request中
		TbUser user = (TbUser) taotaoResult.getData();
		request.setAttribute("user", user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// 
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 
		
	}

}
