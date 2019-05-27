package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
/**
 * 内容Service
 * @author s
 *
 */
public interface ContentService {
	/**
	 * 添加内容
	 * @param content
	 * @return
	 */
	TaotaoResult addContent(TbContent content);
	/**
	 * 通过id获取内容
	 * @param cid
	 * @return
	 */
	List<TbContent> getContentListById(long cid);
}
