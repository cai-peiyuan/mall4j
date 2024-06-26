

package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.shop.bean.app.dto.UserCollectionDto;
import com.yami.shop.bean.model.UserCollection;

/**
 * 用户收藏表
 *
 * @author xwc
 * @date 2019-04-19 16:57:20
 */
public interface UserCollectionMapper extends BaseMapper<UserCollection> {
   /**
    * 分页获取用户收藏
    * @param page
    * @param userId
    * @return
    */
   IPage<UserCollectionDto> getUserCollectionDtoPageByUserId(Page page, String userId);

}
