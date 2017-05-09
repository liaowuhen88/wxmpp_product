package com.baodanyun.websocket.service;

import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Transferlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public class TransferServer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransferlogServer transferlogServer;

    @Autowired
    private UserServer userServer;

    public boolean changeVisitorTo(Transferlog tm) throws BusinessException {

        boolean flag = false;
        try {
            if (!tm.getTransferfrom().equals(tm.getTransferto())) {
                flag = userServer.changeVisitorTo(tm);
                tm.setStatus(flag);
            } else {
                tm.setStatus(flag);
                tm.setDetail("总客服超时，转接忽略");
            }
        } catch (BusinessException e) {
            tm.setStatus(false);
            tm.setDetail(e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            transferlogServer.insert(tm);
        }
        return flag;
    }


}
