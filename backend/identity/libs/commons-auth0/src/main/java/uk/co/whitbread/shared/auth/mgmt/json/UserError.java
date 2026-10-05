package uk.co.whitbread.shared.auth.mgmt.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents an Auth0 User Error object.
 * Related to the {@link uk.co.whitbread.shared.auth.mgmt.json.JobErrors}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserError {
    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    public String toString() {
        return code + " -- " + message;
    }
}
