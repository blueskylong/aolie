package com.ranranx.aolie.core.testobj;

import com.ranranx.aolie.core.common.Constants;
import com.ranranx.aolie.core.exceptions.InvalidException;
import com.ranranx.aolie.core.handler.HandleResult;
import com.ranranx.aolie.core.interceptor.IOperInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @Author xxl
 * @Description
 * @Date 2020/8/10 15:11
 * @Version V0.0.1
 **/
@Component
public class DeleteInterceptor3 implements IOperInterceptor {
    Logger logger = LoggerFactory.getLogger(DeleteInterceptor3.class);

    /**
     * 是否可以处理
     *
     * @param type
     * @param objExtinfo
     * @return
     */
    @Override
    public boolean isCanHandle(String type, Object objExtinfo) {
        return type.equals(Constants.HandleType.TYPE_DELETE);
    }

    /**
     * 操作前调用,如果返回有内容,则会直接返回
     *
     * @param param
     * @return
     * @throws InvalidException
     */
    @Override
    public HandleResult beforeOper(Object param) throws InvalidException {
        logger.error("--> Delete  " + getOrder() + ":beforeOper");
        return null;
    }

    /**
     * 数据查询过后,整理前调用,如果返回有数据,则直接返回
     *
     * @param param
     * @param result
     * @return
     */
    @Override
    public HandleResult afterOper(Object param, HandleResult result) {
        logger.error("--> Delete  " + getOrder() + ":afterOper");
        return null;
    }

    /**
     * 返回前调用,这里会遍历调用,所以要返回结果,不需要处理的就直接返回
     *
     * @param param
     * @param handleResult
     * @return
     */
    @Override
    public HandleResult beforeReturn(Object param, HandleResult handleResult) {
        logger.error("--> Delete  " + getOrder() + ":beforeReturn");
        return null;
    }

    /**
     * 取得顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 3;
    }
}