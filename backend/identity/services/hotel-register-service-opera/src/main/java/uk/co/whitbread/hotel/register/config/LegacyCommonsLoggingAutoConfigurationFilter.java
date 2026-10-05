package uk.co.whitbread.hotel.register.config;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

public class LegacyCommonsLoggingAutoConfigurationFilter implements AutoConfigurationImportFilter {

    static final String TRACE_LOGGING_CONFIGURATION =
        "uk.co.whitbread.shared.commons.logging.config.TraceLoggingConfiguration";

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = !TRACE_LOGGING_CONFIGURATION.equals(autoConfigurationClasses[i]);
        }
        return matches;
    }
}
