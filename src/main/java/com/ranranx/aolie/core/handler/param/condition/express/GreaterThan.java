package com.ranranx.aolie.core.handler.param.condition.express;

/**
 * @author xxl
 * @version V0.0.1
 * @date 2021/3/11 0011 18:02
 **/
public class GreaterThan extends SingleOperCondition {
    public GreaterThan(String tableName, String fieldName, Object value) {
        super(tableName, fieldName, value);
    }


    @Override
    String getOperSign() {
        return ">";
    }
}
