package com.revitalize.admincontrol.utils;

public final class CnpjUtils {

    private static final int CNPJ_LENGTH = 14;

    private CnpjUtils() {
    }

    public static String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    public static boolean isValid(String rawCnpj) {
        String cnpj = onlyDigits(rawCnpj);
        if (cnpj.length() != CNPJ_LENGTH || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        try {
            int[] digits = cnpj.chars().map(Character::getNumericValue).toArray();
            int firstVerifier = calculateVerifier(digits, 12);
            int secondVerifier = calculateVerifier(digits, 13);
            return digits[12] == firstVerifier && digits[13] == secondVerifier;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static int calculateVerifier(int[] digits, int position) {
        int[] weights = position == 12
                ? new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += digits[i] * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
