package com.taotao.content.service.impl;
import java.util.ArrayList;
import java.util.Date;
/**
 * 内容分类Service
 */
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		
		TbContentCategoryExample example=new TbContentCategoryExample();
		//查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> result = new ArrayList<>();
		for (TbContentCategory c : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(c.getId());
			node.setText(c.getName());
			node.setState(c.getIsParent() ? "closed": "open");
			result.add(node);
		}
		
		return result;
	}

	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		//获取对象
		TbContentCategory contentCategory = new TbContentCategory();
		//将信息封装到对象中
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		contentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)',
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		contentCategory.setIsParent(false);
		contentCategoryMapper.insert(contentCategory);
		//判断父节点状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()) {
			//如果父节点为叶子节点应该改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		category.setId(id);
		category.setName(name);
		
		contentCategoryMapper.updateByPrimaryKeySelective(category);
		return TaotaoResult.ok(category);
		
	}

}
