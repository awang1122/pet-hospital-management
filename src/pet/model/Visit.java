package pet.model;

import java.sql.Timestamp;

/**
 * 就诊记录实体类
 */
public class Visit {
    private int id;
    private int petId;
    private Timestamp visitTime;
    private String diagnosis;
    private String prescription;
    private String record;

    public Visit() {}

    public Visit(int petId, Timestamp visitTime, String diagnosis,
                 String prescription, String record) {
        this.petId = petId;
        this.visitTime = visitTime;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.record = record;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public Timestamp getVisitTime() { return visitTime; }
    public void setVisitTime(Timestamp visitTime) { this.visitTime = visitTime; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getRecord() { return record; }
    public void setRecord(String record) { this.record = record; }
}
