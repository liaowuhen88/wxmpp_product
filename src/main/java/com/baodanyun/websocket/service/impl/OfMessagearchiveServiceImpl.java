package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.pageSearch.OfMessagearchiveSearchPage;
import com.baodanyun.websocket.dao.OfMessagearchiveMapper;
import com.baodanyun.websocket.model.OfMessagearchiveWithBLOBs;
import com.baodanyun.websocket.service.OfMessagearchiveService;
import com.baodanyun.websocket.util.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/1.
 */
@Service
public class OfMessagearchiveServiceImpl implements OfMessagearchiveService {
    @Autowired
    private OfMessagearchiveMapper ofMessagearchiveMapper;


    @Override
    public PageInfo<OfMessagearchiveWithBLOBs> select(OfMessagearchiveSearchPage pageSearch) {

        int pageNo = pageSearch.getPageNum() == 0 ? 1 : pageSearch.getPageNum();
        int pageSize = pageSearch.getPageSize() == 0 ? 10 : pageSearch.getPageSize();

        PageHelper.startPage(pageNo, pageSize);
        List<OfMessagearchiveWithBLOBs> list = ofMessagearchiveMapper.select(pageSearch.getJid());
        //用PageInfo对结果进行包装
        PageInfo<OfMessagearchiveWithBLOBs> page = new PageInfo<OfMessagearchiveWithBLOBs>(list);


        return page;
    }

    @Override
    public Long getGroupMessageCount(String date) {
        Long start = null;
        Long end = null;
        if (StringUtils.isNotEmpty(date)) {
            start = DateUtils.dateToUnixTimestamp(date + " 00:00:00");
            end = DateUtils.dateToUnixTimestamp(date + " 23:56:56");
        }

        return ofMessagearchiveMapper.getGroupMessageCount(start, end);
    }

}
