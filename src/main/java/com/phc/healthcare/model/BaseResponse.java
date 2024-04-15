package com.phc.healthcare.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class BaseResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean apiStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> values;

}
