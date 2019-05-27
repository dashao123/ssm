package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

/**
 * 内容分类接口
 * @author s
 *
 */
public interface ContentCategoryService {
	/**
	 * 获得内容分类列表
	 * @return
	 */
	List<EasyUITreeNode> getContentCategoryList(long parentId);
	/**
	 * 添加分类信息
	 * @param parentId
	 * @param name
	 * @return
	 */
	TaotaoResult addContentCategory(Long parentId,String name);
	/**
	 * 更新分类信息
	 * @param id
	 * @param name
	 */
	TaotaoResult updateContentCategory(Long id,String name);
}
