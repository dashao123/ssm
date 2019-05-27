package com.taotao.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;
/**
 * 监听商品添加事件,同步索引库
 * @author s
 *
 */
public class ItemAddMessageListener implements MessageListener {
	
	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		//从消息中查询商品id
		TextMessage textMessage = (TextMessage) message;
		try {
			String text = textMessage.getText();
			Long itemId = Long.parseLong(text);
			//根据商品id查询商品信息
			//等待事务的提交
			Thread.sleep(1000);
			SearchItem item = searchItemMapper.getItemById(itemId);
			
			
			SolrInputDocument document = new SolrInputDocument();
			//向文档中0添加域
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			document.addField("item_desc", item.getItem_desc());
			//提交索引域
			solrServer.add(document);
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
