

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yami.shop.common.serializer.json.ImgJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lanhai
 */
@Data
@TableName("tz_category")
public class Category implements Serializable {

    /**
     * 类目ID
     *
     */
    @TableId

    private Long categoryId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 父节点
     */
    private Long parentId = 0L;

    /**
     * 产品类目名称
     */
    private String categoryName;

    /**
     * 类目图标
     */
    private String icon;

    /**
     * 类目的显示图片
     */
    @JsonSerialize(using = ImgJsonSerializer.class)
    private String pic;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 默认是1，表示正常状态,0为下线状态
     */
    private Integer status;

    /**
     * 记录时间
     */
    private Date recTime;

    /**
     * 分类层级
     */
    private Integer grade;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 品牌id
     */
    @TableField(exist=false)
    private List<Long> brandIds;

    /**
     * 参数id
     */
    @TableField(exist=false)
    private List<Long> attributeIds;

    /**
     * 品牌列表
     */
    @TableField(exist=false)
    private List<Brand> brands;

    /**
     * 参数列表
     */
    @TableField(exist=false)
    private List<ProdProp> prodProps;

    /**
     * 商品列表
     */
    @TableField(exist=false)
    private List<Product> products;

    @TableField(exist=false)
    private List<Category> categories;
}
