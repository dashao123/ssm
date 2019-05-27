package com.taotao.testsolrj;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {
	@Test
	public void testAddDocument() throws Exception{
		//创建一个SolrServer对象.创建一个HttpsolrServer对象
		//需要指定solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		//创建一个文档SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		//向文档中添加域,必须有id域,域的名称必须在schema.xml中定义
		document.addField("id", "123");
		document.addField("item_title", "测试商品1");
		document.addField("item_price", 1000);
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
}
