package com.phc.healthcare.controller;

import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Patient;
import com.phc.healthcare.model.PatientListResponse;
import com.phc.healthcare.service.PatientService;
import com.phc.healthcare.utils.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("patient")
public class PatientController {

    @Autowired
    PatientService patientService;
    private static final BaseResponse bR = new BaseResponse<>();
    private static final PatientListResponse plR = new PatientListResponse();
    private static final String expireMessage = "INVALID OR EXPIRED TOKEN";

    @PostMapping("create")
    public ResponseEntity<BaseResponse> createPatient(@RequestHeader(name = "TOKEN") String token, @RequestBody Patient patient) {
        if (TokenManager.isTokenValid(token)) return patientService.createPatient(patient);
        else {
            bR.setMessage(expireMessage);
            return new ResponseEntity<>(bR, HttpStatus.OK);
        }
    }

    @GetMapping("list")
    public ResponseEntity<PatientListResponse> getAllPatients(@RequestHeader(name = "TOKEN") String token) {
        if (TokenManager.isTokenValid(token)) return patientService.getAllPatients();
        else {
            plR.setMessage(expireMessage);
            return new ResponseEntity<>(plR, HttpStatus.OK);
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<BaseResponse> getPatientById(@RequestHeader(name = "TOKEN") String token, @PathVariable Integer id) {
        if (TokenManager.isTokenValid(token)) return patientService.getPatientById(id);
        else {
            bR.setMessage(expireMessage);
            return new ResponseEntity<>(bR, HttpStatus.OK);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<BaseResponse> deletePatientById(@RequestHeader(name = "TOKEN") String token, @PathVariable Integer id) {
        if (TokenManager.isTokenValid(token)) return patientService.deletePatientById(id);
        else {
            bR.setMessage(expireMessage);
            return new ResponseEntity<>(bR, HttpStatus.OK);
        }
    }

    @GetMapping("vaccinated")
    public ResponseEntity<BaseResponse> vaccinated(@RequestHeader(name = "TOKEN") String token, @RequestParam("id") Integer id, @RequestParam("vaccineName") String vaccineName) {
        if (TokenManager.isTokenValid(token)) return patientService.vaccinated(id, vaccineName);
        else {
            bR.setMessage(expireMessage);
            return new ResponseEntity<>(bR, HttpStatus.OK);
        }
    }

    @GetMapping("scheduler")
    public ResponseEntity<BaseResponse> getScheduler(@RequestHeader(name = "TOKEN") String token) {
        if (TokenManager.isTokenValid(token)) return patientService.getScheduler();
        else {
            bR.setMessage(expireMessage);
            return new ResponseEntity<>(bR, HttpStatus.OK);
        }
    }

}
