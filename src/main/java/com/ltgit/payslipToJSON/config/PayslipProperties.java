package com.ltgit.payslipToJSON.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payslip")
public class PayslipProperties {
    private int maxLines;
    private List<Integer> sectionLengths;

    // Getters and setters
    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public List<Integer> getSectionLengths() {
        return sectionLengths;
    }

    public void setSectionLengths(List<Integer> sectionLengths) {
        this.sectionLengths = sectionLengths;
    }
}
