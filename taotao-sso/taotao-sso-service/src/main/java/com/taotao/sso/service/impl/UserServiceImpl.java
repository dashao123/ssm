package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;
/**
 * 验证登录用户Service
 * @author s
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${TOKEN_EXPIRE}")
	private Integer TOKEN_EXPIRE;
	@Override
	public TaotaoResult checkData(String data, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		if(type == 1) {
			criteria.andUsernameEqualTo(data);
		}else if(type == 2){
			criteria.andPhoneEqualTo(data);
		}else if(type == 3){
			criteria.andEmailEqualTo(data);
		}else {
			TaotaoResult.build(400, "参数申请包含非法参数!");
		}
		
		List<TbUser> list = userMapper.selectByExample(example );
		
		if(list != null && list.size() > 0) {
			//查询到数据,返回False
			return TaotaoResult.ok(false);
		}
		return TaotaoResult.ok(true);
	}

	@Override
	public TaotaoResult register(TbUser user) {
		//判断注册用户的名字,密码,邮箱,电话是否为空重复
		if(StringUtils.isBlank(user.getUsername())) {
			TaotaoResult.build(400, "用户名不能为空");
		}
		TaotaoResult taotaoResult = checkData(user.getUsername(), 1);
		if(!(boolean) taotaoResult.getData()) {
			return TaotaoResult.build(400, "用户名重复");
		}
		if(StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		if(StringUtils.isNotBlank(user.getPhone())) {
			taotaoResult = checkData(user.getPhone(),2);
			if(!(boolean) taotaoResult.getData()) {
				return TaotaoResult.build(400, "电话重复");
			}
		}
		if(StringUtils.isNotBlank(user.getEmail())) {
			taotaoResult = checkData(user.getEmail(),3);
			if(!(boolean) taotaoResult.getData()) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}
		//补全pojo其他属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		String password = DigestUtils.md5Hex(user.getPassword().getBytes());
		user.setPassword(password);
		//插入数据
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(String username, String password) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//校验用户和密码
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0) {
			TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		if(!DigestUtils.md5Hex(password.getBytes()).equals(user.getPassword()) ) {
			TaotaoResult.build(400, "用户名或密码错误");
		}
		//生成token,使用uuid
		String token = UUID.randomUUID().toString();
		//清空密码
		user.setPassword(null);
		//把用户信息保存到redis中
		jedisClient.set(USER_SESSION+":"+token , JsonUtils.objectToJson(user));
		jedisClient.expire(USER_SESSION+":"+token, TOKEN_EXPIRE);
		//返回登录成功,吧token返回
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(USER_SESSION+":"+token);
		
		if(StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "用户身份信息过期!");
		}
		//从新设置过期时间
		jedisClient.expire(USER_SESSION+":"+token, TOKEN_EXPIRE);
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
	}

	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(USER_SESSION+":"+token, 0);
		return TaotaoResult.ok();
	}

}
