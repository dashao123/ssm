package com.taotao.service;
/**
 * 获取商品信息接口
 */
import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;

public interface ItemCatService {
	/**
	 * 获得商品列表
	 * @param parentId
	 * @return
	 */
	List<EasyUITreeNode> getItemCatList(long parentId);
}
