package com.ltgit.payslipToJSON.dto;

import com.ltgit.payslipToJSON.model.payslip.Payslip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private String fileName;
    private int payslipCount;
    private String fileSize;
    private List<Payslip> payslips;
    private String timestamp;
}
