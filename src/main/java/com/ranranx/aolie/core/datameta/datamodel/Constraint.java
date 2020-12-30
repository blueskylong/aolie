package com.ranranx.aolie.core.datameta.datamodel;

import com.ranranx.aolie.core.datameta.dto.ConstraintDto;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

/**
 * @Author xxl
 * @Description
 * @Date 2020/8/5 17:36
 * @Version V0.0.1
 **/
public class Constraint {
    private ConstraintDto constraintDto;

    public Constraint() {

    }

    public Constraint(ConstraintDto constraintDto) {
        this.constraintDto = constraintDto;
    }

    /**
     * 引用到的表,方便查询
     */

    private List<Long> lstRefTable;
    /**
     * 引用到的列,方便查询
     */

    private List<Long> lstRefColumn;

    /**
     * 这里主要是为了更新公式
     *
     * @param columnIds
     */
    public void columnIdChanged(Map<Long, Long> columnIds) {
        //TODO update formula
    }

    @Transient
    public List<Long> getLstRefTable() {
        return lstRefTable;
    }

    public void setLstRefTable(List<Long> lstRefTable) {
        this.lstRefTable = lstRefTable;
    }

    @Transient
    public List<Long> getLstRefColumn() {
        return lstRefColumn;
    }

    public void setLstRefColumn(List<Long> lstRefColumn) {
        this.lstRefColumn = lstRefColumn;
    }


    public ConstraintDto getConstraintDto() {
        return constraintDto;
    }

    public void setConstraintDto(ConstraintDto constraintDto) {
        this.constraintDto = constraintDto;
    }
}
