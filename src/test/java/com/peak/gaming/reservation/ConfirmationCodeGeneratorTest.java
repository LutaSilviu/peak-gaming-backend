package com.peak.gaming.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ConfirmationCodeGeneratorTest {

    private static final Pattern CODE_PATTERN = Pattern.compile("^PK-[ACDEFHJKLMNPRTUVWXY379]{3}\\d{2}$");

    private final ConfirmationCodeGenerator generator = new ConfirmationCodeGenerator();

    @Test
    void generatesCodeMatchingTheFrontendFormat() {
        String code = generator.generate();
        assertThat(code).matches(CODE_PATTERN);
    }

    @Test
    void generatesDifferentCodesAcrossCalls() {
        var codes = java.util.stream.Stream.generate(generator::generate)
                .limit(50)
                .collect(java.util.stream.Collectors.toSet());

        // Not a strict uniqueness guarantee (the space is large enough that
        // collisions in 50 draws are practically impossible), but this catches
        // a generator that's accidentally deterministic or constant.
        assertThat(codes).hasSizeGreaterThan(40);
    }
}
