package com.phc.healthcare.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class LoginResponse extends BaseResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserWrapper user;

}
