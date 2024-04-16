package com.phc.healthcare.controller;

import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Patient;
import com.phc.healthcare.model.PatientListResponse;
import com.phc.healthcare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @PostMapping("create")
    public ResponseEntity<BaseResponse> createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @GetMapping("patients")
    public ResponseEntity<PatientListResponse> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("patient/{id}")
    public ResponseEntity<BaseResponse> getPatientById(@PathVariable Integer id) {
        return patientService.getPatientById(id);
    }

    @DeleteMapping("patient/{id}")
    public ResponseEntity<BaseResponse> deletePatientById(@PathVariable Integer id) {
        return patientService.deletePatientById(id);
    }

    @GetMapping("vaccinated")
    public ResponseEntity<BaseResponse> vaccinated(@RequestParam("id") Integer id, @RequestParam("vaccineName") String vaccineName) {
        return patientService.vaccinated(id, vaccineName);
    }

    @GetMapping("scheduler")
    public ResponseEntity<BaseResponse> getScheduler() {
        return patientService.getScheduler();
    }

}
