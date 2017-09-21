package com.baodanyun.websocket.bean.request;

/**
 * Created by liaowuhen on 2017/8/2.
 */
public class PageRequestBean {
    private Integer offset;
    private Integer limit = 10;
    private Integer page;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
        if (null == offset || 0 == offset) {
            offset = getLimit() * (getPage() - 1);
        }
    }
}
