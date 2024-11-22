package com.ltgit.payslipToJSON.model.payslip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payslip {
    private HeaderPayslip header;
    private BodyPayslip body;
}
