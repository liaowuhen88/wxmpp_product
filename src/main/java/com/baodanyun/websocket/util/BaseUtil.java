package com.baodanyun.websocket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yutao on 2016/7/12.
 */
public class BaseUtil {
    public static Logger logger = LoggerFactory.getLogger(BaseUtil.class);

    public static Integer ObjectToInt(Object o) {

        Integer redi = null;
        if (o instanceof String) {
            redi = (new Double((String) o)).intValue();
        } else if (o instanceof Double) {
            redi = ((Double) o).intValue();
        } else if (o instanceof Integer) {
            redi = (Integer) o;
        }

        return redi;
    }

}
