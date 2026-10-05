package uk.co.whitbread.hotel.register.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LegacyCommonsLoggingAutoConfigurationFilterTest {

    private final LegacyCommonsLoggingAutoConfigurationFilter underTest =
        new LegacyCommonsLoggingAutoConfigurationFilter();

    @Test
    void shouldExcludeLegacyCommonsLoggingConfiguration() {
        boolean[] matches = underTest.match(new String[] {
            LegacyCommonsLoggingAutoConfigurationFilter.TRACE_LOGGING_CONFIGURATION,
            "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"
        }, null);

        assertThat(matches).containsExactly(false, true);
    }
}
