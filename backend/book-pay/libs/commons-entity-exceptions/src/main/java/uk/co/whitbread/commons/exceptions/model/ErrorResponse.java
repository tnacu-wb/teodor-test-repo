package uk.co.whitbread.commons.exceptions.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Integer errCode;
    private String debugMessage;
    private String globalErrTextTemplate;
    private List<ValidationError> details;

    private record ValidationError(String elementId, String errTextTemplate) {
    }

    public void addValidationError(String elementId, String errTextTemplate){
        if(Objects.isNull(details)){
            details = new ArrayList<>();
        }
        details.add(new ValidationError(elementId, errTextTemplate));
    }

}
