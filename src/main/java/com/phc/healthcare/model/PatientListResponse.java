package com.phc.healthcare.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class PatientListResponse extends BaseResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int total;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int maleCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int femaleCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int generalCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int pregnantCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int infantCount;

}
