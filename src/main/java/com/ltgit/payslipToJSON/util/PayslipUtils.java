package com.ltgit.payslipToJSON.util;

public class PayslipUtils {
        // Helper method to format file size
        public static String formatFileSize(long sizeInBytes) {
            if (sizeInBytes < 1024) {
                return sizeInBytes + " bytes";
            } else if (sizeInBytes < 1048576) {
                return sizeInBytes / 1024 + " KB";
            } else {
                return sizeInBytes / 1048576 + " MB";
            }
        }

        public static boolean isAllString(String section) {
            return section.trim().matches("[A-Za-z]+");
        }
    
        public static boolean isAllNumber(String section) {
            return section.trim().matches("[0-9,\\.]+");
        }
    
        public static boolean isMixedContent(String section) {
            boolean containsText = section.matches(".*[A-Za-z]+.*");
            boolean containsNumbers = section.matches(".*[0-9,\\.]+.*");
            return containsText && containsNumbers;
        }
    
        public static String extractNumbers(String section) {
            return section.replaceAll("[^0-9,\\.]", "").replaceAll("\\s+", "");
        }
    
        public static boolean isAllWhitespace(String section) {
            return section.trim().isEmpty();
        }
        
}
