package com.ltgit.payslipToJSON.model.payslip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeaderPayslip {
    private String company;
    private String payslip;
    private String co;
    private String div;
    private String dptm;
    private String periodEnding;
    private String employeeNumber;
    private String employeeName;
}
