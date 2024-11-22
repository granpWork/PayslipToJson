package com.ltgit.payslipToJSON.service;

import com.ltgit.payslipToJSON.config.PayslipProperties;
import com.ltgit.payslipToJSON.model.payslip.BodyPayslip;
import com.ltgit.payslipToJSON.model.payslip.HeaderPayslip;
import com.ltgit.payslipToJSON.model.payslip.Payslip;
import com.ltgit.payslipToJSON.util.PayslipUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PayslipToJsonService {

    private static final Logger log = LoggerFactory.getLogger(PayslipToJsonService.class);

    @Autowired
    private PayslipProperties payslipProperties;

    public Map<String, Object> processPayslipFile(MultipartFile file) {
        if (!file.getContentType().equals("text/plain")) {
            throw new InvalidFileFormatException("Only text files are supported.");
        }

        Map<String, Object> response = new HashMap<>();
        List<List<String>> payslipItems = parseFile(file);

        normalizePayslipItems(payslipItems);

        // Convert payslip items to Payslip objects
        List<Payslip> processedPayslips = convertToPayslipObjects(payslipItems);

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        response.put("fileName", file.getOriginalFilename());
        response.put("fileSize", PayslipUtils.formatFileSize(file.getSize()));
        response.put("payslips", processedPayslips);
        response.put("payslipCount", processedPayslips.size());
        response.put("timestamp", formattedDateTime);

        return response;
    }

    private List<List<String>> parseFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            
            List<String> items = Arrays.asList(sb.toString().split("\f"));
            List<List<String>> payslipItems = new ArrayList<>();
            int maxLines = payslipProperties.getMaxLines(); // Get max lines from the properties

            for (String item : items) {
                boolean found = false;
                int lineCount = 0;
                String[] linesInItem = item.split("\n");
                List<String> processedItems = new ArrayList<>();

                for (String lineInItem : linesInItem) {
                    if (!found) {
                        if (lineInItem.contains("LT GROUP, INC.")) {
                            found = true;
                        }
                    }

                    if (found && lineCount < maxLines) {
                        processedItems.add(lineInItem);
                        lineCount++;
                    }
                }

                if (!processedItems.isEmpty()) {
                    payslipItems.add(processedItems);
                }
            }
            return payslipItems;
        } catch (Exception e) {
            throw new FileProcessingException("Error reading the file content.", e);
        }
    }

    private void normalizePayslipItems(List<List<String>> payslipItems) {
        processHeaderLines(payslipItems);
        processBodyLines(payslipItems);

        processPayslipItemsBodyLines(payslipItems);
        //remove empty lines
        payslipItemsCleanLines(payslipItems);
        
        //merge all Lines
        payslipItemsMergeLines(payslipItems);
    }

    private void processHeaderLines(List<List<String>> payslipItems) {
        for (List<String> payslip : payslipItems) {
            for (int i = 0; i < Math.min(6, payslip.size()); i++) {
                payslip.set(i, payslip.get(i).trim().replaceAll("\\s{2,}", ">"));
            }
        }
    }

    private void processBodyLines(List<List<String>> payslipItems) {
        int maxLines = payslipProperties.getMaxLines();
        for (List<String> payslip : payslipItems) {
            for (int i = 6; i < Math.min(maxLines, payslip.size()); i++) {
                payslip.set(i, ensureLineLength(payslip.get(i)));
            }
        }
    }

    private String ensureLineLength(String line) {
        if (line.length() < 96) {
            return String.format("%-96s", line);
        } else if (line.length() > 96) {
            return line.substring(0, 96);
        }
        return line;
    }

    private static void processPayslipItemsBodyLines(List<List<String>> payslipItems) {
        for (List<String> payslip : payslipItems) {
        	for (int i = 6; i <= 24; i++) {
        		
        		String line = payslip.get(i);
        		payslip.set(i, processLine(line));
            }
        }
    }

    private void payslipItemsCleanLines(List<List<String>> payslipItems) {
        for (List<String> payslip : payslipItems) {
        	for (int i = payslip.size() - 1; i >= 0; i--) {
        		
        		if (payslip.get(i).trim().isEmpty()) {
        			payslip.remove(i);
        		}
        	}
        }	
	}

    private void payslipItemsMergeLines(List<List<String>> payslipItems) {
        for (List<String> payslip : payslipItems) {
            // Create a StringBuilder to accumulate the merged line
            StringBuilder mergedLine = new StringBuilder();

            // Iterate over each line in the payslip and append it to the StringBuilder
            for (String line : payslip) {
                // Add the line followed by '>', except for the last one
                mergedLine.append(line.trim()).append(">");
            }

            // Remove the last '>' if it exists
            if (mergedLine.length() > 0) {
                mergedLine.setLength(mergedLine.length() - 1); // Remove the last '>'
            }

            // Clear the payslip and add the merged line as a single string
            payslip.clear();
            payslip.add(mergedLine.toString());
        }
	}

    private static String processLine(String line) {
        log.debug("Processing line 8: {}", line);

        String section1 = line.substring(0, 32);
        String section2 = line.substring(32, 67);
        String section3 = line.substring(67, 96);

        String processedSection1 = processSection(section1);
        String processedSection2 = processSection(section2);
        String processedSection3 = processSection(section3);

        String finalResult = String.join(">", processedSection1, processedSection2, processedSection3);

        return finalResult;
    }

    private static String processSection(String section) {
        // Trim the section to remove leading and trailing whitespace
        section = section.trim();

        // Handle cases with all whitespace
        if (section.isEmpty()) {
            return "0";
        }

        // Handle cases with only alphabetic characters (strings)
        if (PayslipUtils.isAllString(section)) {
            return "0";
        }

        // Handle cases with only numeric content
        if (PayslipUtils.isAllNumber(section)) {
            return section.replaceAll("\\s+", ""); // Remove all spaces from numbers
        }

        // Handle cases with mixed content
        if (PayslipUtils.isMixedContent(section)) {
            return PayslipUtils.extractNumbers(section); // Extract only the numeric part
        }

        // Default: return "0" if no valid processing rule matches
        return "0";
    }

    private List<Payslip> convertToPayslipObjects(List<List<String>> payslipItems) {
        List<Payslip> processedPayslips = new ArrayList<>();
        for (List<String> payslip : payslipItems) {
            for (String item : payslip) {
                String[] data = item.split(">");
                Payslip payslipObj = Payslip.builder()
                        .header(HeaderPayslip.builder()
                                .company(data[0])
                                .payslip(data[1])
                                .co(data[2])
                                .div(data[3])
                                .dptm(data[4])
                                .periodEnding(data[5])
                                .employeeNumber(data[6])
                                .employeeName(data[7])
                                .build())
                        .body(BodyPayslip.builder()
                                .regHrs(data[8])
                                .wTax(data[9])
                                .status(data[10])
    
                                .regPay(data[11])
                                .sssCont(data[12])
                                .exUnit(data[13])
    
                                .otndHrs(data[14])
                                .medicare(data[15])
                                .rate(data[16])
    
                                .otndPay(data[17])
                                .pagibigFund(data[18])
                                //space data[19]
    
                                .cola(data[20])
                                .pagibigLoan(data[21])
                                .sl(data[22])
    
                                .otherEarn(data[23])
                                .sssLoan(data[24])
                                .vl(data[25])
    
                                .taxAdjustment(data[26])
                                .eispFund(data[27])
                                .ssl(data[28])
    
                                .subsidy(data[29])
                                .unOtrDed(data[30])
                                .pr1(data[31])
    
                                .gratFaSp(data[32])
                                .vale(data[33])
                                .pr2(data[34])
    
                                .oeNtax(data[35])
                                .lostTool(data[36])
                                .tarHrs(data[37])
    
                                //space data[38]
                                .telAdvances(data[39])
                                .absDays(data[40])
    
                                //space data[41]
                                .provEe(data[42])
                                //space data[43]
    
                                //space data[44]
                                .SSSCalamity(data[45])
                                .ytdTaxablePay(data[46])
    
                                //space data[47]
                                .mdb(data[48])
                                .ytdWTax(data[49])
    
                                .kabalikat(data[50])
                                .newYorkLife(data[51])
                                .ltdEispFund(data[52])
    
                                //space data[53]
                                .valueCare(data[54])
                                //space data[55]
    
                                //space data[56]
                                .heritage(data[57])
                                //space data[58]
    
                                //space data[59]
                                //space data[60]
                                //space data[61]
                                
                                .total1(data[62])
                                .total2(data[63])
                                .netPay(data[64])
                                .build())
                            .build();
                processedPayslips.add(payslipObj);
            }
        }
        return processedPayslips;
    }

    // Custom exceptions
    public static class InvalidFileFormatException extends RuntimeException {
        public InvalidFileFormatException(String message) {
            super(message);
        }
    }

    public static class InvalidLineLengthException extends RuntimeException {
        public InvalidLineLengthException(String message) {
            super(message);
        }
    }

    public static class FileProcessingException extends RuntimeException {
        public FileProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
