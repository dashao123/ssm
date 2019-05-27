package com.taotao.search.service;

import com.taotao.common.pojo.SearchResult;
/**
 * 搜索商品接口
 * @author s
 *
 */
public interface SearchService {
	SearchResult search(String queryString,int page,int rows) throws Exception;
}
