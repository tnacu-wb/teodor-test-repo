package uk.co.whitbread.common.exceptions.mapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix="ms.error")
public class ErrorCodeMappingConfig {


    @Value("${ms.error.default.defaultMessage:Unmapped Error Code}")
    public String defaultMessage;
    @Value("${ms.error.default.defaultHttpStatus:500}")
    public int defaultHttpStatus;
    @Value("${ms.error.default.defaultCode:999}")
    public String defaultCode;

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }


    public int getDefaultHttpStatus() {
        return defaultHttpStatus;
    }

    public void setDefaultHttpStatus(int defaultHttpStatus) {
        this.defaultHttpStatus = defaultHttpStatus;
    }


    public List<ErrorCodeMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<ErrorCodeMapping> mappings) {
        this.mappings = mappings;
    }

    List<ErrorCodeMapping> mappings;

    public ErrorCodeMapping getDefaultErrorCodeMapping(){

        ErrorCodeMapping errorCodeMapping = new ErrorCodeMapping();
        errorCodeMapping.setCode(defaultCode);
        errorCodeMapping.setHttpStatus(defaultHttpStatus);
        errorCodeMapping.setMessage(defaultMessage);

        return errorCodeMapping;

    }
}
