package uk.co.whitbread.shared.cdh.model;

import java.util.List;

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
public class UpdateApplicationRequest {

    private String applicationId;
    private String resumeUrl;
    private String updateDate;
    private String stage;
    private String hostedPageGuid;
    private String directDebitOption;

    @JsonProperty("Participants")
    private List<Participants> participants;

}
