package com.phc.healthcare.model;

import lombok.Data;

@Data
public class Scheduler {

    private String[] vaccines;
    private Object vaccineDays;
    private Object[] duration;

}
