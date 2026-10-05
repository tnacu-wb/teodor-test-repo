package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartApplicationRequest {

    private String applicationId;
    private String applicationNumber;
    private String applicationGuid;
    private String startedDate;
    private int companyId;
    private String accountName;
    private String scheme;
    private String stage;

    @JsonProperty("Participants")
    private Participants participants;

}
