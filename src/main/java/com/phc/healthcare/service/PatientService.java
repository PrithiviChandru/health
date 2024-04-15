package com.phc.healthcare.service;

import com.google.gson.Gson;
import com.phc.healthcare.dao.PatientDAO;
import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Patient;
import com.phc.healthcare.model.PatientListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientService {

    private String[] vaccines = {"BCG", "OPV-Ze80 dose", "Hep-B Birth dose", "OPV-1", "Pentavalent-1", "Rota-1", "flPV-1", "(PVC-1)", "OPV-2", "Pentavalent-2", "Rota-2", "OPV-3", "Pentavalent-3", "Rota-3", "flPV-2", "(PVC-2)", "MR 1st Dose", "Vit-A", "JE-1* (PVC-B)", "DPT First Booster dose", "OPV Booster Dose", "MR-2", "JE-2*", "DPT Second Booster dose", "Td Single dose (10 Years)", "Td Single dose (16 Years)", "Td1 Early in pregnancy", "Td2 Four Weeks after Td1", "Td Booster if received 2Td dose in a pregnancy within the last 3 years"};

    @Autowired
    PatientDAO patientDAO;

    public ResponseEntity<BaseResponse> createPatient(Patient patient) {

        ResponseEntity<BaseResponse> response;
        BaseResponse<Patient> br = new BaseResponse<>();

        try {

            String pType = patient.getPatientType();
            String pName = patient.getPatientName();
            String pId = patient.getPatientId();
            String dob = patient.getDob();
            int age = patient.getAge();
            String gender = patient.getGender();

            if (null == pType || pType.trim().isEmpty()) throw new IllegalArgumentException("Patient Type Required");
            if (null == pName || pName.trim().isEmpty()) throw new IllegalArgumentException("Patient Name Required");
            if (null == pId || pId.trim().isEmpty()) throw new IllegalArgumentException("Patient Id Required");
            if (null == dob || dob.trim().isEmpty()) throw new IllegalArgumentException("Date of Birth Required");
            if (age == -1) throw new IllegalArgumentException("Age Required");
            if (null == gender || gender.trim().isEmpty()) throw new IllegalArgumentException("Gender Required");

            boolean pvIsEmpty = patient.getPreviousVaccine().trim().isEmpty();
            boolean pvaIsEmpty = patient.getPreviousAdministration().trim().isEmpty();
            boolean nvIsEmpty = patient.getNextVaccine().isEmpty();
            boolean nvaIsEmpty = patient.getNextAdministration().trim().isEmpty();

            if (!pvIsEmpty || !pvaIsEmpty) {
                if (pvIsEmpty) throw new IllegalArgumentException("Previous Vaccine Required");
                if (pvaIsEmpty) throw new IllegalArgumentException("Previous Administration Required");
            }

            if (!nvIsEmpty || !nvaIsEmpty) {
                if (nvIsEmpty) throw new IllegalArgumentException("Next Vaccine Required");
                if (nvaIsEmpty) throw new IllegalArgumentException("Next Administration Required");
            }

            Map<String, Boolean> map = new LinkedHashMap<>();
            for (int i = 0; i < vaccines.length; i++) {
                map.put(vaccines[i], false);
            }

            Gson gson = new Gson();
            String status = gson.toJson(map);
            patient.setVaccineStatus(status);

            Patient pRes = patientDAO.save(patient);
            br.setApiStatus(true);
            br.setValue(pRes);

        } catch (IllegalArgumentException e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } catch (Exception e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(br, HttpStatus.ACCEPTED);
        }

        return response;
    }

    public ResponseEntity<PatientListResponse> getAllPatients() {

        ResponseEntity<PatientListResponse> response;
        PatientListResponse plR = new PatientListResponse();

        try {

            int mCount = 0;
            int fmCount = 0;
            int gnCount = 0;
            int pgCount = 0;
            int ifCount = 0;

            List<Patient> patients = patientDAO.findAll();
            for (int i = 0; i < patients.size(); i++) {

                Patient p = patients.get(i);
                if (p.getGender().equals("Male")) mCount++;
                else if (p.getGender().equals("Female")) fmCount++;

                if (p.getPatientType().equals("General")) gnCount++;
                else if (p.getPatientType().equals("Pregnant")) pgCount++;
                else if (p.getPatientType().equals("Infant")) ifCount++;

            }

            plR.setApiStatus(true);
            plR.setValues(patients);
            plR.setTotal(patients.size());
            plR.setMaleCount(mCount);
            plR.setFemaleCount(fmCount);
            plR.setGeneralCount(gnCount);
            plR.setPregnantCount(pgCount);
            plR.setInfantCount(ifCount);

        } catch (Exception e) {
            plR.setApiStatus(false);
            plR.setMessage(e.getMessage());
            plR.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(plR, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<BaseResponse> getPatientById(Integer id) {

        ResponseEntity<BaseResponse> response;
        BaseResponse<Patient> br = new BaseResponse<>();

        try {

            Optional<Patient> patient = patientDAO.findById(id);
            br.setApiStatus(true);
            br.setValue(patient.get());

        } catch (Exception e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(br, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<BaseResponse> deletePatientById(Integer id) {

        ResponseEntity<BaseResponse> response;
        BaseResponse<Patient> br = new BaseResponse<>();

        try {
            patientDAO.deleteById(id);
            br.setApiStatus(true);
        } catch (Exception e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(br, HttpStatus.OK);
        }

        return response;
    }

    public void getScheduler() {

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
