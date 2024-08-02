package com.phc.healthcare.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String patientType;
    private String patientName;
    private String patientId;
    private String dob;
    private int age = -1;
    private String gender;
    private String phone1;
    private String phone2;
    private String motherName;
    private String fatherName;
    private String district;
    private String state;
    private String pinCode;
    private String address;
    private String previousVaccine;
    private String previousAdministration;
    private String nextVaccine;
    private String nextAdministration;
    private String allergies;
    @Column(length = 65555)
    private String vaccineStatus;

}
