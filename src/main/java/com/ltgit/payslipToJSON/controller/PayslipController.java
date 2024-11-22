package com.ltgit.payslipToJSON.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ltgit.payslipToJSON.service.PayslipToJsonService;

@RestController
@RequestMapping("/api/v1")
public class PayslipController {
    private static final Logger log = LoggerFactory.getLogger(PayslipController.class);

    @Autowired
    private PayslipToJsonService payslipService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTextFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            response = payslipService.processPayslipFile(file);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            response.put("error", "Unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
