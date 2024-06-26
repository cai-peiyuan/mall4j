

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.UserAddrDto;
import com.yami.shop.bean.model.UserAddr;

/**
 * @author lanhai
 */
public interface UserAddrService extends IService<UserAddr> {
	/**
	 * 获取用户默认地址
	 * @param userId
	 * @return
	 */
	UserAddr getDefaultUserAddr(String userId);

	/**
	 * 更新默认地址
	 * @param addrId 默认地址id
	 * @param userId 用户id
	 */
	void updateDefaultUserAddr(Long addrId, String userId);

	/**
	 * 删除缓存
	 * @param addrId
	 * @param userId
	 */
    void removeUserAddrByUserId(Long addrId, String userId);

	/**
	 * 根据用户id和地址id获取用户地址
	 * @param addrId
	 * @param userId
	 * @return
	 */
    UserAddr getUserAddrByUserId(Long addrId, String userId);


	/**
	 * 判断用户选择的收货地址是否支持配送
	 * @param userAddr
	 * @return
	 */
	boolean validUserAddr(UserAddrDto userAddr);
}

