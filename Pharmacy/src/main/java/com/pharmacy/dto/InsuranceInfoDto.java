package com.pharmacy.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public class InsuranceInfoDto {

    @Positive
    private long insuranceId;
    @Positive
    private long patientId;

    @NotBlank
    private String provider;
    @NotBlank
    private String memberId;
    @NotBlank
    private String bin;
    @NotBlank
    private String pcn;
    @NotBlank
    private String groupId;

    public InsuranceInfoDto() {}

    public InsuranceInfoDto(String provider, String memberId, String bin, String pcn, String groupId) {
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

}
