package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.pageSearch.MessagearchiveAdapterSearchPage;
import com.baodanyun.websocket.dao.MessageArchiveAdapterMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
import com.baodanyun.websocket.service.MessageFiterService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/21.
 */

@Service
public class MessageArchiveAdapterServiceImpl implements MessageArchiveAdapterService {
    @Autowired
    private MessageArchiveAdapterMapper messageArchiveAdapterMapper;
    @Autowired
    private MessageFiterService messageFiterService;

    @Override
    public int insert(MessageArchiveAdapter record) {
        return messageArchiveAdapterMapper.insert(record);
    }

    @Override
    public MessageArchiveAdapter selectByMessageId(String messageid) {
        return messageArchiveAdapterMapper.selectByMessageId(messageid);
    }

    @Override
    public PageInfo<MessageArchiveAdapter> selectByFromAndTo(MessagearchiveAdapterSearchPage pageSearch) throws BusinessException {

        if (StringUtils.isEmpty(pageSearch.getFromJid()) || StringUtils.isEmpty(pageSearch.getToJid())) {
            throw new BusinessException("参数异常");
        }
        int pageNo = pageSearch.getPageNum() == 0 ? 1 : pageSearch.getPageNum();
        int pageSize = pageSearch.getPageSize() == 0 ? 10 : pageSearch.getPageSize();

        PageHelper.startPage(pageNo, pageSize);
        MessageArchiveAdapter search = new MessageArchiveAdapter();
        search.setFromJid(pageSearch.getFromJid());
        search.setToJid(pageSearch.getToJid());

        if (!messageFiterService.isEncrypt(pageSearch.getToJid(), pageSearch.getFromJid())) {
            List<MessageArchiveAdapter> list = messageArchiveAdapterMapper.selectByFromAndTo(search);
            //用PageInfo对结果进行包装
            PageInfo<MessageArchiveAdapter> page = new PageInfo<>(list);

            if (null != page.getList() && page.getList().size() > 0) {
                for (MessageArchiveAdapter ma : page.getList()) {
                    messageFiterService.computationalCosts(ma.getToJid(), ma);
                    messageArchiveAdapterMapper.deleteByPrimaryKey(ma.getId());
                }
            }
            return page;
        } else {
            throw new BusinessException("解密条件不满足");
        }
    }

    @Override
    public int deleteByMessageId(String messageid) {
        return messageArchiveAdapterMapper.deleteByMessageId(messageid);
    }
}
