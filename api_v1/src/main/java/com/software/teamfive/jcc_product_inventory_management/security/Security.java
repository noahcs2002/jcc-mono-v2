package com.software.teamfive.jcc_product_inventory_management.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Helper class to handle some basic security needs
 */
public class Security {
    final private static PasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Given a string, season and then encode it using Base64 encoding and two season strings
     * @param inString The string to season and encode.
     * @return Seasoned and encoded String.
     */
    public static String encode(String inString) {
        return encoder.encode(inString);
    }

    public static boolean matches(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }
}