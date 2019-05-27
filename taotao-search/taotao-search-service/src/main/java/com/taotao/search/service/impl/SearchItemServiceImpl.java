package com.taotao.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;
/**
 * 商品数据导入索引库
 * @author s
 *
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {
	
	@Autowired
	private SearchItemMapper itemServiceMapper;
	
	@Autowired
	private SolrServer solrServer;
	@Override
	public TaotaoResult importItemToIndex() {
		
		try {
			//先获得商品列表
			List<SearchItem> itemList = itemServiceMapper.getItemList();
			System.out.println("1");
			//创建索引
			//循环遍历,添加索引
			for (SearchItem item : itemList) {
				//创建一个文档SolrInputDocument
				SolrInputDocument document = new SolrInputDocument();
				//向文档中0添加域
				document.addField("id", item.getId());
				document.addField("item_title", item.getTitle());
				document.addField("item_sell_point", item.getSell_point());
				document.addField("item_price", item.getPrice());
				document.addField("item_image", item.getImage());
				document.addField("item_category_name", item.getCategory_name());
				document.addField("item_desc", item.getItem_desc());
				solrServer.add(document);
			}
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "数据倒入失败");
		}
		//返回添加成功
		System.out.println("2");
		return TaotaoResult.ok();
	}

}
