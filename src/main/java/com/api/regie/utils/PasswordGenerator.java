package com.api.regie.utils;

import java.util.UUID;

public class PasswordGenerator {

    public static String generatePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

}
