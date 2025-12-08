package com.group6.vietravel.data.models.place;

import com.google.gson.annotations.SerializedName;

public class District {
    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private int code;

    @SerializedName("division_type")
    private String divisionType;

    @SerializedName("codename")
    private String codename;

    @SerializedName("province_code")
    private int provinceCode;

    // --- Getter & Setter ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    // Override toString để hiển thị tên trực tiếp lên Spinner/ListView
    @Override
    public String toString() {
        return name;
    }
}
