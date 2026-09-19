package com.peak.gaming.reservation;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/** Mirrors the frontend's booking-confirmation.ts generateConfirmationCode(): "PK-" + 3 letters + 2 digits. */
@Component
public class ConfirmationCodeGenerator {

    private static final String CODE_CHARS = "ACDEFHJKLMNPRTUVWXY379";
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            letters.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        int digits = 10 + random.nextInt(89);
        return "PK-" + letters + digits;
    }
}
