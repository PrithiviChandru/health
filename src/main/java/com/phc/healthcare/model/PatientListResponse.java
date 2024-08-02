package com.phc.healthcare.model;

import lombok.Data;

@Data
public class PatientListResponse extends BaseResponse {

    private int total = -1;
    private int maleCount = -1;
    private int femaleCount = -1;
    private int generalCount = -1;
    private int pregnantCount = -1;
    private int infantCount = -1;

}
