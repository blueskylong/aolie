package com.ranranx.aolie.datameta.datamodel;

import com.ranranx.aolie.datameta.dto.ReferenceDto;

/**
 * @Author xxl
 * @Description
 * @Date 2020/8/5 17:34
 * @Version V0.0.1
 **/
public class Reference {
    private ReferenceDto referenceDto;

    public Reference(ReferenceDto referenceDto) {
        this.referenceDto = referenceDto;
    }

    public ReferenceDto getReferenceDto() {
        return referenceDto;
    }

    public void setReferenceDto(ReferenceDto referenceDto) {
        this.referenceDto = referenceDto;
    }
}