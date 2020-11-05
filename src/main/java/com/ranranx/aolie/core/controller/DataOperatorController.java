package com.ranranx.aolie.core.controller;

import com.ranranx.aolie.core.common.Constants;
import com.ranranx.aolie.core.handler.HandleResult;
import com.ranranx.aolie.core.handler.HandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author xxl
 * @Description
 * @Date 2020/8/10 14:50
 * @Version V0.0.1
 **/
@RequestMapping("/dataService")
@RestController
public class DataOperatorController {
    @Autowired
    private HandlerFactory factory;

    /**
     * 查询数据
     *
     * @param mapParam 此map需要符合查询参数 QueryParam
     * @return
     */
    @PostMapping("/findData")
    public HandleResult findData(Map<String, Object> mapParam) {
        return factory.getHandler(Constants.HandleType.TYPE_QUERY, mapParam)
                .doHandle(mapParam);
    }

    /**
     * 新增数据
     *
     * @param mapParam  此map需要符合查询参数 InsertParam
     * @return
     */
    @PostMapping("/insert")
    public HandleResult insert(Map<String, Object> mapParam) {
        return factory.getHandler(Constants.HandleType.TYPE_INSERT, mapParam)
                .doHandle(mapParam);
    }

    @PostMapping("/update")
    public HandleResult update(Map<String, Object> mapParam) {
        return factory.getHandler(Constants.HandleType.TYPE_UPDATE, mapParam)
                .doHandle(mapParam);
    }

    @PostMapping("/delete")
    public HandleResult delete(Map<String, Object> mapParam) {
        return factory.getHandler(Constants.HandleType.TYPE_DELETE, mapParam)
                .doHandle(mapParam);
    }


}