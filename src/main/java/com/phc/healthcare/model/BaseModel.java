package com.phc.healthcare.model;

import lombok.Data;

@Data
public class BaseModel {

    private String createdBy;
    private String updatedBy;
    private Integer createdStamp;
    private Integer updatedStamp;

}
