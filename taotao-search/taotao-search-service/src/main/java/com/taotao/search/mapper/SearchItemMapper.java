package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

/**
 * 搜索Mapper
 * @author s
 *
 */
public interface SearchItemMapper {
	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
