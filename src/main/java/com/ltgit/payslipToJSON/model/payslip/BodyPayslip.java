package com.ltgit.payslipToJSON.model.payslip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyPayslip {
    //line 1
    private String regHrs;
    private String wTax;
    private String status;

    //line 2
    private String regPay;
    private String sssCont;
    private String exUnit;

    //line 3
    private String otndHrs;
    private String medicare;
    private String rate;

    //line 4
    private String otndPay;
    private String pagibigFund;

    //line 5
    private String cola;
    private String pagibigLoan;
    private String sl;

    //line 6
    private String otherEarn;
    private String sssLoan;
    private String vl;

    //line 7
    private String taxAdjustment;
    private String eispFund;
    private String ssl;

    //line 8
    private String subsidy;
    private String unOtrDed;
    private String pr1;

    //line 9
    private String gratFaSp;
    private String vale;
    private String pr2;

    //line 10
    private String oeNtax;
    private String lostTool;
    private String tarHrs;

    //line 11
    private String telAdvances;
    private String absDays;

    //line 12
    private String provEe;

    //line 13
    private String SSSCalamity;
    private String ytdTaxablePay;

    //line 14
    private String mdb;
    private String ytdWTax;

    //line 15
    private String kabalikat;
    private String newYorkLife;
    private String ltdEispFund;

    //line 16
    private String valueCare;

    //line 17
    private String heritage;

    //line 18
    private String total1;
    private String total2;
    private String netPay;
}
