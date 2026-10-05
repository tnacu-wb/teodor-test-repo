package uk.co.whitbread.shared.auth.mgmt.json;

import com.auth0.json.mgmt.users.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * Class that represents an Auth0 Job Errors object.
 * Related to the {@link uk.co.whitbread.shared.auth.mgmt.JobErrorsEntity} entity.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobErrors {
    @JsonProperty("user")
    private User user;

    @JsonProperty("errors")
    private UserError[] errors;

    @JsonCreator
    private JobErrors(@JsonProperty("user") User user, @JsonProperty("errors") UserError[] errors) {
        this.user = user;
        this.errors = Arrays.copyOf(errors, errors.length);
    }
}
