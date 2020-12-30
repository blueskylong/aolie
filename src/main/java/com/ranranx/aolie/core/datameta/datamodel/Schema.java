package com.ranranx.aolie.core.datameta.datamodel;

import com.ranranx.aolie.core.common.IdGenerator;
import com.ranranx.aolie.core.datameta.dto.*;
import com.ranranx.aolie.core.ds.dataoperator.DataSourceUtils;
import com.ranranx.aolie.core.exceptions.NotExistException;
import com.ranranx.aolie.core.interfaces.SchemaMessageReceiveHandler;
import com.ranranx.aolie.core.interfaces.SchemaMessageSendHandler;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xxl
 * @Description
 * @Date 2020/8/5 17:35
 * @Version V0.0.1
 **/
public class Schema {
    public Schema() {
    }

    public Schema(SchemaDto schemaDto) {
        this.schemaDto = schemaDto;
    }

    private SchemaDto schemaDto;

    private List<TableColumnRelation> lstRelation;
    /**
     * 下属表
     */
    private List<TableInfo> lstTable;
    /**
     * 表间约束
     */
    private List<Constraint> lstConstraint;
    /**
     * 外部消息处理器
     */
    private List<SchemaMessageReceiveHandler> lstReceiveHandler;
    /**
     * 通知外面消息
     */
    private List<SchemaMessageSendHandler> lstSendHandler;
    /**
     * 所有的引用信息
     */
    private List<Reference> lstReference;

    @Transient
    public List<TableDto> getTableDtos() {
        List<TableDto> lstResult = new ArrayList<>();
        if (this.lstTable != null) {
            this.lstTable.forEach((table -> {
                table.getTableDto().setSchemaId(this.getSchemaDto().getSchemaId());
                table.getTableDto().setVersionCode(this.getSchemaDto().getVersionCode());
                lstResult.add(table.getTableDto());
            }));

        }
        return lstResult;
    }

    @Transient
    public List<FormulaDto> getFormulaDtos() {
        List<FormulaDto> lstFormulaDto = new ArrayList<>();
        if (this.lstTable != null) {
            Long schemaId = getSchemaDto().getSchemaId();
            String version = getSchemaDto().getVersionCode();
            lstTable.forEach((table -> {
                List<FormulaDto> formulas = table.getFormulaDtos(schemaId,
                        version);
                if (formulas != null && !formulas.isEmpty()) {
                    lstFormulaDto.addAll(formulas);
                }
            }));
        }
        return lstFormulaDto;
    }

    @Transient
    public List<Formula> getFormulas() {
        List<Formula> lstFormula = new ArrayList<>();
        if (this.lstTable != null) {
            Long schemaId = getSchemaDto().getSchemaId();
            String version = getSchemaDto().getVersionCode();
            lstTable.forEach((table -> {
                List<Formula> formulas = table.getFormulas(schemaId,
                        version);
                if (formulas != null && !formulas.isEmpty()) {
                    lstFormula.addAll(formulas);
                }
            }));
        }
        return lstFormula;
    }

    @Transient
    public List<ColumnDto> getColumnDtos() {
        List<ColumnDto> lstFormulaDto = new ArrayList<>();
        if (this.lstTable != null) {
            Long schemaId = getSchemaDto().getSchemaId();
            String version = getSchemaDto().getVersionCode();
            lstTable.forEach((table -> {
                List<ColumnDto> columnDtos = table.getColumnDtos(schemaId, version);
                if (columnDtos != null && !columnDtos.isEmpty()) {
                    lstFormulaDto.addAll(columnDtos);
                }
            }));
        }
        return lstFormulaDto;
    }

    @Transient
    public List<ConstraintDto> getConstraintDtos() {
        List<ConstraintDto> lstResult = new ArrayList<>();
        if (this.lstConstraint != null && !this.lstConstraint.isEmpty()) {
            Long schemaId = getSchemaDto().getSchemaId();
            String version = getSchemaDto().getVersionCode();
            this.lstConstraint.forEach((constraint -> {
                ConstraintDto dto = constraint.getConstraintDto();
                dto.setSchemaId(schemaId);
                dto.setVersionCode(version);
                lstResult.add(dto);
            }));
        }
        return lstResult;
    }

    public List<Reference> getLstReference() {
        return lstReference;
    }

    public String validate() {
        return null;
    }

    public void setLstReference(List<Reference> lstReference) {
        this.lstReference = lstReference;
    }

    public List<Constraint> getLstConstraint() {
        return lstConstraint;
    }

    public void setLstConstraint(List<Constraint> lstConstraint) {
        this.lstConstraint = lstConstraint;
    }

    public List<TableInfo> getLstTable() {
        return lstTable;
    }

    public void setLstTable(List<TableInfo> lstTable) {
        this.lstTable = lstTable;
    }

    public SchemaDto getSchemaDto() {
        return schemaDto;
    }

    public void setSchemaDto(SchemaDto schemaDto) {
        this.schemaDto = schemaDto;
    }

    public List<SchemaMessageReceiveHandler> getLstReceiveHandler() {
        return lstReceiveHandler;
    }

    public void setLstReceiveHandler(List<SchemaMessageReceiveHandler> lstReceiveHandler) {
        this.lstReceiveHandler = lstReceiveHandler;
    }

    public List<SchemaMessageSendHandler> getLstSendHandler() {
        return lstSendHandler;
    }

    public void setLstSendHandler(List<SchemaMessageSendHandler> lstSendHandler) {
        this.lstSendHandler = lstSendHandler;
    }

    public List<TableColumnRelation> getLstRelation() {
        return lstRelation;
    }

    public void setLstRelation(List<TableColumnRelation> lstRelation) {
        this.lstRelation = lstRelation;
    }

    @Transient
    public List<TableColumnRelationDto> getRelationDto() {
        if (this.lstRelation != null && !this.lstRelation.isEmpty()) {
            List<TableColumnRelationDto> lstDto = new ArrayList<>();
            this.lstRelation.forEach(relation -> {
                lstDto.add(relation.getDto());
            });
            return lstDto;
        }
        return null;
    }

    @Transient
    public List<ReferenceDto> getReferenceDto() {
        if (!SchemaTools.isReferenceSchema(this.getSchemaDto().getSchemaId())) {
            return null;
        }
        List<ReferenceDto> lstDto = new ArrayList<>();
        int index = 1;
        if (this.getLstTable() != null) {
            for (TableInfo tableInfo : this.getLstTable()) {
                List<ReferenceDto> lstReference = tableInfo.getLstReference();
                if (lstReference == null || lstReference.isEmpty()) {
                    continue;
                }
                for (ReferenceDto dto : lstReference) {
                    if (dto.getRefId() < 0) {
                        dto.setRefId(IdGenerator.getNextId(Reference.class.getName()));
                    }
                    dto.setTableName(tableInfo.getTableDto().getTableName());
                    dto.setXh(index++);
                    lstDto.add(dto);
                }
            }
            return lstDto;
        }
        return null;
    }

    @Transient
    public String getDsKey() {
        if (this.getSchemaDto().getDataOperId() == null || this.getSchemaDto().getDataOperId() == 0) {
            return DataSourceUtils.getDefaultDataSourceKey();
        }
        DataOperatorInfo dataOperatorInfo = SchemaHolder.getDataOperatorInfo(schemaDto.getDataOperId(), schemaDto.getVersionCode());
        if (dataOperatorInfo == null) {
            throw new NotExistException("数据库连接:[" + schemaDto.getDataOperId() + "__" + schemaDto.getVersionCode() + "]不存在");
        }
        return dataOperatorInfo.getDsKey();
    }

    /**
     * 查找指定表的关系信息,使用组合去查询
     *
     * @param tableIds
     * @return
     */
    @Transient
    public List<TableColumnRelation> getTablesRelation(Long... tableIds) {
        if (tableIds == null || tableIds.length < 2) {
            return null;
        }
        TableColumnRelation relation;
        List<TableColumnRelation> lstResult = new ArrayList<>();
        for (int i = 0; i < tableIds.length - 1; i++) {
            for (int j = i + 1; j < tableIds.length; j++) {
                relation = findTableRelation(tableIds[i], tableIds[j]);
                if (relation != null) {
                    lstResult.add(relation);
                }
            }
        }
        return lstResult;
    }

    /**
     * 查询二张表的关系
     *
     * @param table1
     * @param table2
     * @return
     */
    private TableColumnRelation findTableRelation(long table1, long table2) {
        List<TableColumnRelation> lstRelation = this.getLstRelation();
        if (lstRelation == null || lstRelation.isEmpty()) {
            return null;
        }
        for (TableColumnRelation tableColumnRelation : lstRelation) {
            if ((tableColumnRelation.getTableFrom().getTableDto().getTableId() == table1 &&
                    tableColumnRelation.getTableTo().getTableDto().getTableId() == table2)) {
                return tableColumnRelation;
            }
            if (tableColumnRelation.getTableFrom().getTableDto().getTableId() == table2 &&
                    tableColumnRelation.getTableTo().getTableDto().getTableId() == table1) {
                return tableColumnRelation;
            }
        }
        return null;
    }
}
