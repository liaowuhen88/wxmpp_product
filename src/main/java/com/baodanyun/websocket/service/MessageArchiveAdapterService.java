package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.pageSearch.MessagearchiveAdapterSearchPage;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.github.pagehelper.PageInfo;

/**
 * Created by liaowuhen on 2017/7/21.
 */
public interface MessageArchiveAdapterService {
    int insert(MessageArchiveAdapter record);

    MessageArchiveAdapter selectByMessageId(String messageid);

    PageInfo<MessageArchiveAdapter> selectByFromAndTo(MessagearchiveAdapterSearchPage page) throws BusinessException;

    int deleteByMessageId(String messageid);
}
