package com.shabanaj.beloyal.Dto.Registration;

import java.time.LocalDate;

public class WaiterProfileRegisterDto {
    private String employeeCode;
    private LocalDate hireDate;

    public WaiterProfileRegisterDto(String employeeCode, LocalDate hireDate) {
        this.employeeCode = employeeCode;
        this.hireDate = hireDate;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
