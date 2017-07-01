package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.pageSearch.OfMessagearchiveSearchPage;
import com.baodanyun.websocket.model.OfMessagearchiveWithBLOBs;
import com.github.pagehelper.PageInfo;

/**
 * Created by liaowuhen on 2017/7/1.
 */
public interface OfMessagearchiveService {
    PageInfo<OfMessagearchiveWithBLOBs> select(OfMessagearchiveSearchPage page);
}
