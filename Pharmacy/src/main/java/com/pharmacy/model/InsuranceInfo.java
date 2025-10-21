package com.pharmacy.model;

import java.util.Objects;

public class InsuranceInfo {

    private long insuranceId;
    private long patientId;

    private String provider;
    private String memberId;
    private String bin;
    private String pcn;
    private String groupId;

    public InsuranceInfo() {}

    public InsuranceInfo(String provider, String memberId, String bin, String pcn, String groupId) {
        this.provider = provider;
        this.memberId = memberId;
        this.bin = bin;
        this.pcn = pcn;
        this.groupId = groupId;
    }

    public long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(long insuranceId) {
        this.insuranceId = insuranceId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getPcn() {
        return pcn;
    }

    public void setPcn(String pcn) {
        this.pcn = pcn;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceInfo that = (InsuranceInfo) o;
        return insuranceId == that.insuranceId && patientId == that.patientId && Objects.equals(provider, that.provider) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(bin, that.bin) &&
                Objects.equals(pcn, that.pcn) &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceId, patientId, provider, memberId, bin, pcn, groupId);
    }

    @Override
    public String toString() {
        return "InsuranceInfo{" +
                "insuranceId=" + insuranceId +
                ", patientId=" + patientId +
                ", provider ='" + provider + '\'' +
                ", memberId ='" + memberId + '\'' +
                ", bin ='" + bin + '\'' +
                ", pcn ='" + pcn + '\'' +
                ", groupId ='" + groupId + '\'' +
                '}';
    }

}