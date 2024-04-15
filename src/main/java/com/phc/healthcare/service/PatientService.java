package com.phc.healthcare.service;

import com.google.gson.Gson;
import com.phc.healthcare.dao.PatientDAO;
import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Patient;
import com.phc.healthcare.model.PatientListResponse;
import com.phc.healthcare.model.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

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
            Scheduler scheduler = (Scheduler) getScheduler().getBody().getValue();
            for (int i = 0; i < scheduler.getVaccines().length; i++) {
                map.put(scheduler.getVaccines()[i], false);
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
            response = new ResponseEntity<>(br, HttpStatus.OK);
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

            Patient patient = patientDAO.findById(id).get();
            br.setApiStatus(true);
            br.setValue(patient);

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

    public ResponseEntity<BaseResponse> vaccinated(Integer id, String vaccineName) {

        ResponseEntity<BaseResponse> response;
        BaseResponse<Patient> br = new BaseResponse<>();
        Gson gson = new Gson();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {

            if (vaccineName.trim().isEmpty()) throw new IllegalArgumentException("Vaccine Name Required");
            Patient patient = patientDAO.findById(id).get();

            Scheduler scheduler = (Scheduler) getScheduler().getBody().getValue();
            String[] vaccines = scheduler.getVaccines();
            Map<String, String> vaccineDays = (Map<String, String>) scheduler.getVaccineDays();

            int vaccineIndex = -1;
            for (int i = 0; i < vaccines.length; i++) {
                if (vaccines[i].equals(vaccineName)) {
                    vaccineIndex = i;
                    break;
                }
            }

            if (vaccineIndex < 0) throw new IllegalArgumentException("Vaccine Not Found");
            int nextVaccineIndex = vaccineIndex + 1;
            String nextVaccine = vaccines[nextVaccineIndex];

            Instant instant = LocalDate.now().atStartOfDay(ZoneOffset.of("+05:30")).toInstant();
            String pvAdministered = instant.atZone(ZoneId.of("+05:30")).format(formatter);
            long pvAdministeredStamp = instant.toEpochMilli();

            Map<String, Boolean> vaccineStatus = (Map<String, Boolean>) gson.fromJson(patient.getVaccineStatus(), Object.class);
            if (vaccineStatus.get(vaccineName)) throw new IllegalArgumentException("Already vaccinated");
            vaccineStatus.put(vaccineName, true);

            patient.setVaccineStatus(gson.toJson(vaccineStatus));
            patient.setPreviousVaccine(vaccineName);
            patient.setNextVaccine(nextVaccine);
            patient.setPreviousAdministration(pvAdministered);

            System.out.println(patient.getDob());
            System.out.println(vaccineDays.get(nextVaccine));

            br.setApiStatus(true);
            br.setValue(patient);

        } catch (IllegalArgumentException e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } catch (Exception e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(br, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<BaseResponse> getScheduler() {

        ResponseEntity<BaseResponse> response;
        BaseResponse<Scheduler> br = new BaseResponse<>();

        try {

            Scheduler scheduler = new Scheduler();
            Gson gson = new Gson();

            String[] vaccines = {"BCG", "OPV-Ze80 dose", "Hep-B Birth dose", "OPV-1", "Pentavalent-1", "Rota-1", "flPV-1", "(PVC-1)", "OPV-2", "Pentavalent-2", "Rota-2", "OPV-3", "Pentavalent-3", "Rota-3", "flPV-2", "(PVC-2)", "MR 1st Dose", "Vit-A", "JE-1* (PVC-B)", "DPT First Booster dose", "OPV Booster Dose", "MR-2", "JE-2*", "DPT Second Booster dose", "Td Single dose (10 Years)", "Td Single dose (16 Years)", "Td1 Early in pregnancy", "Td2 Four Weeks after Td1", "Td Booster if received 2Td dose in a pregnancy within the last 3 years"};
            String days = "{\"BCG\":\"14\",\"OPV-Ze80 dose\":\"28\",\"Hep-B Birth dose\":\"42\",\"OPV-1\":\"47\",\"Pentavalent-1\":\"52\",\"Rota-1\":\"57\",\"flPV-1\":\"62\",\"(PVC-1)\":\"67\",\"OPV-2\":\"79\",\"Pentavalent-2\":\"88\",\"Rota-2\":\"97\",\"OPV-3\":\"131\",\"Pentavalent-3\":\"164\",\"Rota-3\":\"197\",\"flPV-2\":\"230\",\"(PVC-2)\":\"263\",\"MR 1st Dose\":\"336\",\"Vit-A\":\"402\",\"JE-1* (PVC-B)\":\"468\",\"DPT First Booster dose\":\"547\",\"OPV Booster Dose\":\"608\",\"MR-2\":\"669\",\"JE-2*\":\"730\",\"DPT Second Booster dose\":\"2007\",\"Td Single dose (10 Years)\":\"3650\",\"Td Single dose (16 Years)\":\"5840\",\"Td1 Early in pregnancy\":\"--\",\"Td2 Four Weeks after Td1\":\"--\",\"Td Booster if received 2Td dose in a pregnancy within the last 3 years\":\"--\"}";
            String[] duration = {"{\"duration\":\"At Birth\",\"vaccines\":[\"BCG\",\"OPV-Ze80 dose\",\"Hep-B Birth dose\"]}", "{\"duration\":\"6 Week\",\"vaccines\":[\"OPV-1\",\"Pentavalent-1\",\"Rota-1\",\"flPV-1\",\"(PVC-1)\"]}", "{\"duration\":\"10 Week\",\"vaccines\":[\"OPV-2\",\"Pentavalent-2\",\"Rota-2\"]}", "{\"duration\":\"14 Week\",\"vaccines\":[\"OPV-3\",\"Pentavalent-3\",\"Rota-3\",\"flPV-2\",\"(PVC-2)\"]}", "{\"duration\":\"9 Months\",\"vaccines\":[\"MR 1st Dose\",\"Vit-A\",\"JE-1* (PVC-B)\"]}", "{\"duration\":\"16-24 Months\",\"vaccines\":[\"DPT First Booster dose\",\"OPV Booster Dose\",\"MR-2\",\"JE-2*\"]}", "{\"duration\":\"5-6 Years\",\"vaccines\":[\"DPT Second Booster dose\"]}", "{\"duration\":\"10 Years\",\"vaccines\":[\"Td Single dose (10 Years)\"]}", "{\"duration\":\"16 Years\",\"vaccines\":[\"Td Single dose (16 Years)\"]}", "{\"duration\":\"Pregnant Mothers\",\"vaccines\":[\"Td1 Early in pregnancy\",\"Td2 Four Weeks after Td1\",\"Td Booster if received 2Td dose in a pregnancy within the last 3 years\"]}"};

            Object daysObj = gson.fromJson(days, Object.class);
            Object[] durationObj = new Object[duration.length];

            for (int i = 0; i < duration.length; i++) {
                String d = duration[i];
                durationObj[i] = gson.fromJson(d, Object.class);
            }

            scheduler.setVaccines(vaccines);
            scheduler.setVaccineDays(daysObj);
            scheduler.setDuration(durationObj);

            br.setApiStatus(true);
            br.setValue(scheduler);

        } catch (Exception e) {
            br.setApiStatus(false);
            br.setMessage(e.getMessage());
            br.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(br, HttpStatus.OK);
        }

        return response;
    }

}
