package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
/**
 * 验证用户
 * @author s
 *
 */
public interface UserService {
	TaotaoResult checkData(String data, int type);
	TaotaoResult register(TbUser user);
	TaotaoResult login(String username,String password);
	TaotaoResult getUserByToken(String token);
	TaotaoResult logout(String token);
}
