package com.taota.test.pagehelper;

import java.util.List;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;


public class TestPageHelper {
//	@Test
	public void testHelper() {
		//初始化spring容器
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		//获取mapper的代理对象
		TbItemMapper itemMapper = context.getBean(TbItemMapper.class);
		
		//分页信息
		PageHelper.startPage(1, 3);//3行,紧跟着的第一个会被分页
		
		//调用mapper的方法查询数据
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		List<TbItem> list2 = itemMapper.selectByExample(example);
		
	
		//获得分页信息
		PageInfo<TbItem> info = new PageInfo<TbItem>(list);
		
		System.out.println("第一个分页list集合长度"+list.size());
		System.out.println("第二个分页list集合长度"+list2.size());
		
//		//遍历集合
//		for (TbItem tbItem : list) {
//			System.out.println(tbItem.getCid()+">>>>"+tbItem.getTitle());
//		}
	}
}
