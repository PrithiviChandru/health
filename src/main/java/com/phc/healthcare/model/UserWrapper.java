package com.phc.healthcare.model;

import lombok.Data;

@Data
public class UserWrapper {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public UserWrapper(Integer id, String email, String firstName, String lastName, String phone) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
}
