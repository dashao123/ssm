package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService {
	
	//注入Mapper
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate  jmsTemplate;
	@Resource(name="itemAddtopic")
	private Destination destination;
	@Autowired
	private JedisClient jedisClient;

	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	@Value("${TIEM_EXPIRE}")
	private Integer TIEM_EXPIRE;
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		if(page == null)page=1;
		if(rows == null)rows=30;
		//设置分页信息
		PageHelper.startPage(page, rows);
		//创建example对象  不需要设置查询条件
		TbItemExample example = new TbItemExample();
		//根据Mapper查询所有数据方法
		List<TbItem> list = itemMapper.selectByExample(example );
		PageInfo<TbItem> info = new PageInfo<>(list);
		//封装EasyUIDataGridResult对象中国
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(info.getList());
		result.setTotal((int)info.getTotal());
		return result;
	}
	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成商品id
		final long itemId = IDUtils.genItemId();
		//补全item属性
		item.setId(itemId);
		//商品状态 1.正常,2.下架,3.删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//想商品插入数据
		itemMapper.insert(item);
		//创建商品描述表对应的pojo
		TbItemDesc itemDesc = new TbItemDesc();
		//补全pojo
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(itemId);
		
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//向activemq发送商品添加消息
		jmsTemplate.send(destination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				//发送商品id
				TextMessage message = session.createTextMessage(itemId+"");
				
				return message;
			}
		});
		//返回结果
		return TaotaoResult.ok();
	}
	@Override
	public TbItem getItemById(long itemId) {
		//先在缓存中查
		try {
			String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":BASE");
			if(StringUtils.isNotBlank(json)) {
				//把json转换成item对象
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//缓存中没有数据
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		try {
			//将数据库中信息进行缓存
			jedisClient.set(ITEM_INFO + ":" + itemId + ":BASE", JsonUtils.objectToJson(item));
			//设置过期时间,提高缓存的利用率
			jedisClient.expire(TIEM_EXPIRE + ":" + itemId + ":BASE", TIEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//查询数据库之前先查询缓存
		try {
			String json = jedisClient.get(ITEM_INFO + ":" + itemId  + ":DESC");
			if (StringUtils.isNotBlank(json)) {
				// 把json数据转换成pojo
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有查询数据库
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			//把查询结果添加到缓存
			jedisClient.set(ITEM_INFO + ":" + itemId  + ":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间，提高缓存的利用率
			jedisClient.expire(ITEM_INFO + ":" + itemId  + ":DESC", TIEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	
	}

}
