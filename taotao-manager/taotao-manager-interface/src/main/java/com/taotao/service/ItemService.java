package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
/**
 * 商品相关处理接口
 * @author s
 */
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
public interface ItemService {
	/**
	 * 根据当前页码,和每页的行数进行分页
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult getItemList(Integer page,Integer rows);
	/**
	 * 添加商品
	 * @param item
	 * @param desc
	 * @return
	 */
	TaotaoResult addItem(TbItem item,String desc);
	
	TbItem getItemById(long itemId);
	TbItemDesc getItemDescById(long itemId);
}
