package com.grocery.quickbasket.utils;

public class OrderCodeGenerator {
    private static final String ORDER_CODE_PREFIX = "ORD";
    private static final int ORDER_CODE_LENGTH = 12;

    public static String generateCode() {
        StringBuilder sb = new StringBuilder(ORDER_CODE_PREFIX);

        // Add timestamp (last 6 digits of current time in milliseconds)
        sb.append(String.format("%06d", System.currentTimeMillis() % 1000000));

        // Add random digits to reach desired length
        while (sb.length() < ORDER_CODE_LENGTH) {
            sb.append((int) (Math.random() * 10));
        }

        return sb.toString();
    }
}
