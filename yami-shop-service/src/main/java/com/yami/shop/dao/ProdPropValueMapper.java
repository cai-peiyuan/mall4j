

package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.ProdPropValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lanhai
 */
public interface ProdPropValueMapper extends BaseMapper<ProdPropValue> {

	/**
	 * 插入商品属性数值
	 * @param propId
	 * @param prodPropValues
	 */
	void insertPropValues(@Param("propId") Long propId, @Param("prodPropValues") List<ProdPropValue> prodPropValues);

	/**
	 * 删除属性数值
	 * @param propId
	 */
	void deleteByPropId(@Param("propId") Long propId);
}
