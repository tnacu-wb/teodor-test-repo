package uk.co.whitbread.hotel.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotEmpty
    @Schema(required = true,  example = "qpQzrHMfVsJcOhSM")
    private String sessionId;

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
